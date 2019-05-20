package com.github.rulegin.actors.session;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.github.rulegin.actors.msg.cluster.ClusterEventMsg;
import com.github.rulegin.actors.msg.core.SessionTimeoutMsg;
import com.github.rulegin.actors.msg.session.SessionAwareMsg;
import com.github.rulegin.actors.msg.session.SessionCloseMsg;
import com.github.rulegin.actors.msg.session.SessionCtrlMsg;
import com.github.rulegin.actors.msg.termination.SessionTerminationMsg;
import com.github.rulegin.actors.service.ContextBasedCreator;
import com.github.rulegin.actors.service.DefaultActorService;
import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.ContextAwareActor;
import com.github.rulegin.common.data.id.SessionId;

import java.util.HashMap;
import java.util.Map;

public class SessionManagerActor extends ContextAwareActor {

    private static final int INITIAL_SESSION_MAP_SIZE = 1024;

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final Map<String, ActorRef> sessionActors;

    public SessionManagerActor(ActorSystemContext systemContext) {
        super(systemContext);
        this.sessionActors = new HashMap<>(INITIAL_SESSION_MAP_SIZE);
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Integer){
            System.out.println("ok u int");
        }
        if (msg instanceof SessionAwareMsg) {
            forwardToSessionActor((SessionAwareMsg) msg);
        } else if (msg instanceof SessionTerminationMsg) {
            onSessionTermination((SessionTerminationMsg) msg);
        } else if (msg instanceof Terminated) {
            onTermination((Terminated) msg);
        } else if (msg instanceof SessionTimeoutMsg) {
            onSessionTimeout((SessionTimeoutMsg) msg);
        } else if (msg instanceof SessionCtrlMsg) {
            onSessionCtrlMsg((SessionCtrlMsg) msg);
        } else if (msg instanceof ClusterEventMsg) {
            broadcast(msg);
        }
    }

    private void broadcast(Object msg) {
        sessionActors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
    }

    private void onSessionTimeout(SessionTimeoutMsg msg) {
        String sessionIdStr = msg.getSessionId().toUidStr();
        ActorRef sessionActor = sessionActors.get(sessionIdStr);
        if (sessionActor != null) {
            sessionActor.tell(msg, ActorRef.noSender());
        }
    }

    private void onSessionCtrlMsg(SessionCtrlMsg msg) {
        String sessionIdStr = msg.getSessionId().toUidStr();
        ActorRef sessionActor = sessionActors.get(sessionIdStr);
        if (sessionActor != null) {
            sessionActor.tell(msg, ActorRef.noSender());
        }
    }

    private void onSessionTermination(SessionTerminationMsg msg) {
        String sessionIdStr = msg.getId().toUidStr();
        ActorRef sessionActor = sessionActors.remove(sessionIdStr);
        if (sessionActor != null) {
            log.debug("[{}] Removed session actors.", sessionIdStr);
            //TODO: onSubscriptionUpdate device actors about session close;
        } else {
            log.debug("[{}] Session actors was already removed.", sessionIdStr);
        }
    }

    private void forwardToSessionActor(SessionAwareMsg msg) {
        if (msg instanceof SessionCloseMsg) {
            String sessionIdStr = msg.getSessionId().toUidStr();
            ActorRef sessionActor = sessionActors.get(sessionIdStr);
            if (sessionActor != null) {
                sessionActor.tell(msg, ActorRef.noSender());
            } else {
                log.debug("[{}] Session actors was already removed.", sessionIdStr);
            }
        } else {
            try {
                getOrCreateSessionActor(msg.getSessionId()).tell(msg, self());
            } catch (InvalidActorNameException e) {
                log.info("Invalid msg : {}", msg);
            }
        }
    }

    private ActorRef getOrCreateSessionActor(SessionId sessionId) {
        String sessionIdStr = sessionId.toUidStr();
        ActorRef sessionActor = sessionActors.get(sessionIdStr);
        if (sessionActor == null) {
            log.debug("[{}] Creating session actors.", sessionIdStr);
            sessionActor = context().actorOf(
                    Props.create(new SessionActor.ActorCreator(systemContext, sessionId)).withDispatcher(DefaultActorService.SESSION_DISPATCHER_NAME),
                    sessionIdStr);
            sessionActors.put(sessionIdStr, sessionActor);
            log.debug("[{}] Created session actors.", sessionIdStr);
        }
        return sessionActor;
    }

    private void onTermination(Terminated message) {
        ActorRef terminated = message.actor();
        if (terminated instanceof LocalActorRef) {
            log.info("Removed actors: {}.", terminated);
            //TODO: cleanup session actors map
        } else {
            throw new IllegalStateException("Remote actors are not supported!");
        }
    }

    public static class ActorCreator extends ContextBasedCreator<SessionManagerActor> {
        private static final long serialVersionUID = 1L;

        public ActorCreator(ActorSystemContext context) {
            super(context);
        }

        @Override
        public SessionManagerActor create() throws Exception {
            return new SessionManagerActor(context);
        }
    }

}
