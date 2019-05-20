
package com.github.rulegin.actors.service.cluster.rpc;

import com.github.rulegin.actors.msg.cluster.ToAllNodesMsg;
import com.github.rulegin.actors.msg.core.RpcBroadcastMsg;
import com.github.rulegin.actors.msg.core.RpcSessionCreateRequestMsg;
import com.github.rulegin.actors.msg.core.TimeoutMsg;
import com.github.rulegin.actors.msg.plugin.aware.PluginRpcMsg;
import com.github.rulegin.actors.service.cluster.discovery.ServerInstance;
import com.github.rulegin.actors.service.cluster.discovery.ServerInstanceService;
import com.github.rulegin.common.data.id.EntityId;
import com.github.rulegin.actors.service.cluster.ServerAddress;
import com.github.rulegin.server.gen.cluster.ClusterAPIProtos;
import com.github.rulegin.server.gen.cluster.ClusterRpcServiceGrpc;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class ClusterGrpcService extends ClusterRpcServiceGrpc.ClusterRpcServiceImplBase implements ClusterRpcService {

    @Autowired
    private ServerInstanceService instanceService;

    private RpcMsgListener listener ;

    private Server server;

    private ServerInstance instance;

    private ConcurrentMap<UUID, RpcSessionCreationFuture> pendingSessionMap = new ConcurrentHashMap<>();

    public void init(RpcMsgListener listener) {
        this.listener = listener;
        log.info("Initializing RPC service!");
        instance = instanceService.getSelf();
        server = ServerBuilder.forPort(instance.getPort()).addService(this).build();
        log.info("Going to start RPC server using port: {}", instance.getPort());
        try {
            server.start();
        } catch (IOException e) {
            log.error("Failed to start RPC server!", e);
            throw new RuntimeException("Failed to start RPC server!");
        }
        log.info("RPC service initialized!");
    }

    @Override
    public void onSessionCreated(UUID msgUid, StreamObserver<ClusterAPIProtos.ToRpcServerMessage> msg) {
        RpcSessionCreationFuture future = pendingSessionMap.remove(msgUid);
        if (future != null) {
            try {
                future.onMsg(msg);
            } catch (InterruptedException e) {
                log.warn("Failed to report created session!");
            }
        } else {
            log.warn("Failed to lookup pending session!");
        }
    }

    @Override
    public StreamObserver<ClusterAPIProtos.ToRpcServerMessage> handlePluginMsgs(StreamObserver<ClusterAPIProtos.ToRpcServerMessage> responseObserver) {
        log.info("Processing new session.");
        return createSession(new RpcSessionCreateRequestMsg(UUID.randomUUID(), null, responseObserver));
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            log.info("Going to onStop RPC server");
            server.shutdownNow();
            try {
                server.awaitTermination();
                log.info("RPC server stopped!");
            } catch (InterruptedException e) {
                log.warn("Failed to onStop RPC server!");
            }
        }
    }

    @Override
    public void tell(PluginRpcMsg toForward) {
        ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
                .setToPluginRpcMsg(toProtoMsg(toForward)).build();
        tell(toForward.getRpcMsg().getServerAddress(), msg);
    }

    @Override
    public void broadcast(ToAllNodesMsg toForward) {
        //封装成RPC message
        ClusterAPIProtos.ToRpcServerMessage msg = ClusterAPIProtos.ToRpcServerMessage.newBuilder()
                .setToAllNodesRpcMsg(toProtoMsg(toForward)).build();
        listener.onMsg(new RpcBroadcastMsg(msg));
    }

    private void tell(ServerAddress serverAddress, ClusterAPIProtos.ToRpcServerMessage msg) {
        listener.onMsg(new TimeoutMsg.RpcSessionTellMsg(serverAddress, msg));
    }

    private StreamObserver<ClusterAPIProtos.ToRpcServerMessage> createSession(RpcSessionCreateRequestMsg msg) {
        RpcSessionCreationFuture future = new RpcSessionCreationFuture();
        pendingSessionMap.put(msg.getMsgUid(), future);
        listener.onMsg(msg);
        try {
            StreamObserver<ClusterAPIProtos.ToRpcServerMessage> observer = future.get();
            log.info("Processed new session.");
            return observer;
        } catch (Exception e) {
            log.info("Failed to process session.", e);
            throw new RuntimeException(e);
        }
    }

    //系列化并封装RPC message
    private ClusterAPIProtos.ToAllNodesRpcMessage toProtoMsg(ToAllNodesMsg msg) {
        return ClusterAPIProtos.ToAllNodesRpcMessage.newBuilder().setData(
                ByteString.copyFrom(SerializationUtils.serialize(msg))
        ).build();
    }


    private ClusterAPIProtos.ToPluginRpcMessage toProtoMsg(PluginRpcMsg msg) {
        return ClusterAPIProtos.ToPluginRpcMessage.newBuilder()
                .setClazz(msg.getRpcMsg().getMsgClazz())
                .setData(ByteString.copyFrom(msg.getRpcMsg().getMsgData()))
                .setAddress(ClusterAPIProtos.PluginAddress.newBuilder()
                       // .setUserId(toUid(msg.getPluginTenantId().getId()))
                        .setPluginId(toUid(msg.getPluginId().getId()))
                        .build()
                ).build();
    }

    private static ClusterAPIProtos.Uid toUid(EntityId uuid) {
        return toUid(uuid.getId());
    }

    private static ClusterAPIProtos.Uid toUid(UUID uuid) {
        return ClusterAPIProtos.Uid.newBuilder().setPluginUuidMsb(uuid.getMostSignificantBits()).setPluginUuidLsb(
                uuid.getLeastSignificantBits()).build();
    }
}
