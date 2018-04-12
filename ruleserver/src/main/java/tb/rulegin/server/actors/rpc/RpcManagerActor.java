package tb.rulegin.server.actors.rpc;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.actors.ContextAwareActor;
import tb.rulegin.server.actors.service.ContextBasedCreator;
import tb.rulegin.server.actors.service.DefaultActorService;
import tb.rulegin.server.actors.service.cluster.discovery.ServerInstance;
import tb.rulegin.server.actors.msg.cluster.ClusterEventMsg;
import tb.rulegin.server.actors.service.cluster.ServerAddress;
import tb.rulegin.server.gen.cluster.ClusterAPIProtos;
import tb.rulegin.server.actors.msg.core.*;

import java.util.*;
import java.util.stream.Stream;


public class RpcManagerActor extends ContextAwareActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final Map<ServerAddress, SessionActorInfo> sessionActors;

    private final Map<ServerAddress, Queue<ClusterAPIProtos.ToRpcServerMessage>> pendingMsgs;

    private final ServerAddress instance;

    public RpcManagerActor(ActorSystemContext systemContext) {
        super(systemContext);
        this.sessionActors = new HashMap<>();
        this.pendingMsgs = new HashMap<>();
        this.instance = systemContext.getDiscoveryService().getCurrentServer().getServerAddress(); //localhost:9001

        Stream<ServerInstance> serverInstance = systemContext.getDiscoveryService().getOtherServers().stream();

        Stream<ServerInstance>  filter = serverInstance.filter(otherServer -> otherServer.getServerAddress().compareTo(instance) > 0);
        filter.forEach(otherServer ->{
                log.info(otherServer.getHost()+"  "+otherServer.getServerAddress().toString());
                onCreateSessionRequest(new RpcSessionCreateRequestMsg(UUID.randomUUID(), otherServer.getServerAddress(), null));});
//        log.info("filter.count():"+filter.count());


    }

    @Override
    public void onReceive(Object msg) throws Exception {
        log.info("RpcManagerActor received msg : {}",msg);
        if (msg instanceof Integer){
            System.out.println("ok u int");
        }
        if (msg instanceof TimeoutMsg.RpcSessionTellMsg) {
            onMsg((TimeoutMsg.RpcSessionTellMsg) msg);
        }else if (msg instanceof RpcSessionConnectedMsg) {
            onSessionConnected((RpcSessionConnectedMsg) msg);
        } else if (msg instanceof RpcBroadcastMsg) {
            //rpc  broadcast
            log.info("RpcBroadcastMsg");
            onMsg((RpcBroadcastMsg) msg);
        } else if (msg instanceof RpcSessionCreateRequestMsg) {
            onCreateSessionRequest((RpcSessionCreateRequestMsg) msg);
        }  else if (msg instanceof RpcSessionDisconnectedMsg) {
            onSessionDisconnected((RpcSessionDisconnectedMsg) msg);
        } else if (msg instanceof RpcSessionClosedMsg) {
            onSessionClosed((RpcSessionClosedMsg) msg);
        } else if (msg instanceof ClusterEventMsg) {
            onClusterEvent((ClusterEventMsg) msg);
        }
    }

    private void onMsg(RpcBroadcastMsg msg) {
        log.info("Forwarding msg to session actors {}", msg);
        log.info("sessionActors.size() "+sessionActors.size());
        log.info("pendingMsgs.size()"+pendingMsgs.size());
        sessionActors.keySet().forEach(address -> onMsg(new TimeoutMsg.RpcSessionTellMsg(address, msg.getMsg())));
        pendingMsgs.values().forEach(queue -> queue.add(msg.getMsg()));
    }

    private void onMsg(TimeoutMsg.RpcSessionTellMsg msg) {
        log.info("TimeoutMsg.RpcSessionTellMsg {}"+msg );
        ServerAddress address = msg.getServerAddress();
        SessionActorInfo session = sessionActors.get(address);
        if (session != null) {
            log.info("{} Forwarding msg to session actors", address);
            // RpcSessionActor
            session.actor.tell(msg, ActorRef.noSender());
        } else {
            log.info("{} Storing msg to pending queue", address);
            Queue<ClusterAPIProtos.ToRpcServerMessage> queue = pendingMsgs.get(address);
            if (queue == null) {
                queue = new LinkedList<>();
                pendingMsgs.put(address, queue);
            }
            queue.add(msg.getMsg());
        }
    }

    @Override
    public void postStop() {
        sessionActors.clear();
        pendingMsgs.clear();
    }

    private void onClusterEvent(ClusterEventMsg msg) {
        ServerAddress server = msg.getServerAddress();
        if (server.compareTo(instance) > 0) {
            if (msg.isAdded()) {
                onCreateSessionRequest(new RpcSessionCreateRequestMsg(UUID.randomUUID(), server, null));
            } else {
                onSessionClose(false, server);
            }
        }
    }

    private void onSessionConnected(RpcSessionConnectedMsg msg) {
        register(msg.getRemoteAddress(), msg.getId(), context().sender());
    }

    private void onSessionDisconnected(RpcSessionDisconnectedMsg msg) {
        boolean reconnect = msg.isClient() && isRegistered(msg.getRemoteAddress());
        onSessionClose(reconnect, msg.getRemoteAddress());
    }

    private void onSessionClosed(RpcSessionClosedMsg msg) {
        boolean reconnect = msg.isClient() && isRegistered(msg.getRemoteAddress());
        onSessionClose(reconnect, msg.getRemoteAddress());
    }

    private boolean isRegistered(ServerAddress address) {
        for (ServerInstance server : systemContext.getDiscoveryService().getOtherServers()) {
            if (server.getServerAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    private void onSessionClose(boolean reconnect, ServerAddress remoteAddress) {
        log.info("[{}] session closed. Should reconnect: {}", remoteAddress, reconnect);
        SessionActorInfo sessionRef = sessionActors.get(remoteAddress);
        if (context().sender().equals(sessionRef.actor)) {
            sessionActors.remove(remoteAddress);
            pendingMsgs.remove(remoteAddress);
            if (reconnect) {
                onCreateSessionRequest(new RpcSessionCreateRequestMsg(sessionRef.sessionId, remoteAddress, null));
            }
        }
    }

    private void onCreateSessionRequest(RpcSessionCreateRequestMsg msg) {
        ActorRef actorRef = createSessionActor(msg);
        if (msg.getRemoteAddress() != null) {
            log.info("onCreateSessionRequest:");
            register(msg.getRemoteAddress(), msg.getMsgUid(), actorRef);
        }
    }

    private void register(ServerAddress remoteAddress, UUID uuid, ActorRef sender) {
        sessionActors.put(remoteAddress, new SessionActorInfo(uuid, sender));
        log.info("[{}][{}] Registering session actors.", remoteAddress, uuid);
        Queue<ClusterAPIProtos.ToRpcServerMessage> data = pendingMsgs.remove(remoteAddress);
        if (data != null) {
            log.info("[{}][{}] Forwarding {} pending messages.", remoteAddress, uuid, data.size());
            data.forEach(msg -> sender.tell(new TimeoutMsg.RpcSessionTellMsg(remoteAddress, msg), ActorRef.noSender()));
        } else {
            log.info("[{}][{}] No pending messages to forward.", remoteAddress, uuid);
        }
    }

    private ActorRef createSessionActor(RpcSessionCreateRequestMsg msg) {
        log.info("[{}] Creating session actors.", msg.getMsgUid());
        ActorRef actor = context().actorOf(
                Props.create(new RpcSessionActor.ActorCreator(systemContext, msg.getMsgUid())).withDispatcher(DefaultActorService.RPC_DISPATCHER_NAME));
        actor.tell(msg, context().self());
        return actor;
    }

    public static class ActorCreator extends ContextBasedCreator<RpcManagerActor> {
        private static final long serialVersionUID = 1L;

        public ActorCreator(ActorSystemContext context) {
            super(context);
        }

        @Override
        public RpcManagerActor create() throws Exception {
            return new RpcManagerActor(context);
        }
    }
}
