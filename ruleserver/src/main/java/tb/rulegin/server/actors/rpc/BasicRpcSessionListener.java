package tb.rulegin.server.actors.rpc;

import akka.actor.ActorRef;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.actors.service.ActorService;
import tb.rulegin.server.actors.service.cluster.rpc.GrpcSession;
import tb.rulegin.server.actors.service.cluster.rpc.GrpcSessionListener;
import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.actors.service.cluster.ServerAddress;
import tb.rulegin.server.actors.msg.cluster.ToAllNodesMsg;
import tb.rulegin.server.actors.msg.plugin.aware.ToPluginRpcResponseDeviceMsg;
import tb.rulegin.server.actors.msg.plugin.aware.PluginRpcMsg;
import tb.rulegin.server.actors.msg.RpcError;
import tb.rulegin.server.actors.msg.core.RpcMsg;
import tb.rulegin.server.actors.msg.core.RpcSessionClosedMsg;
import tb.rulegin.server.actors.msg.core.RpcSessionConnectedMsg;
import tb.rulegin.server.actors.msg.core.RpcSessionDisconnectedMsg;
import lombok.extern.slf4j.Slf4j;
import tb.rulegin.server.gen.cluster.ClusterAPIProtos;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.UUID;

/**
 */
@Slf4j
public class BasicRpcSessionListener implements GrpcSessionListener {

    private final ActorSystemContext context;
    private final ActorService service;
    private final ActorRef manager;
    private final ActorRef self;

    public BasicRpcSessionListener(ActorSystemContext context, ActorRef manager, ActorRef self) {
        this.context = context;
        this.service = context.getActorService();
        this.manager = manager;
        this.self = self;
    }

    @Override
    public void onConnected(GrpcSession session) {
        log.info("{} BasicRpcSessionListener session started -> {}", getType(session), session.getRemoteServer());
        if (!session.isClient()) {
            manager.tell(new RpcSessionConnectedMsg(session.getRemoteServer(), session.getSessionId()), self);
        }
    }

    @Override
    public void onDisconnected(GrpcSession session) {
        log.info("{} session closed -> {}", getType(session), session.getRemoteServer());
        manager.tell(new RpcSessionDisconnectedMsg(session.isClient(), session.getRemoteServer()), self);
    }

    @Override
    public void onToPluginRpcMsg(GrpcSession session, ClusterAPIProtos.ToPluginRpcMessage msg) {
        if (log.isTraceEnabled()) {
            log.trace("{} session [{}] received plugin msg {}", getType(session), session.getRemoteServer(), msg);
        }
        service.onMsg(convert(session.getRemoteServer(), msg));
    }

/*    @Override
    public void onToDeviceActorRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceActorRpcMessage msg) {
        log.trace("{} session [{}] received device actors msg {}", getType(session), session.getRemoteServer(), msg);
        service.onMsg((ToDeviceActorMsg) deserialize(msg.getDataSource().toByteArray()));
    }

    @Override
    public void onToDeviceActorNotificationRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceActorNotificationRpcMessage msg) {
        log.trace("{} session [{}] received device actors notification msg {}", getType(session), session.getRemoteServer(), msg);
        service.onMsg((ToDeviceActorNotificationMsg) deserialize(msg.getDataSource().toByteArray()));
    }

    @Override
    public void onToDeviceSessionActorRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceSessionActorRpcMessage msg) {
        log.trace("{} session [{}] received session actors msg {}", getType(session), session.getRemoteServer(), msg);
        service.onMsg((ToDeviceSessionActorMsg) deserialize(msg.getDataSource().toByteArray()));
    }

    @Override
    public void onToDeviceRpcRequestRpcMsg(GrpcSession session, ClusterAPIProtos.ToDeviceRpcRequestRpcMessage msg) {
        log.trace("{} session [{}] received session actors msg {}", getType(session), session.getRemoteServer(), msg);
        service.onMsg(deserialize(session.getRemoteServer(), msg));
    }

     private static ToDeviceRpcRequestPluginMsg deserialize(ServerAddress serverAddress, ClusterAPIProtos.ToDeviceRpcRequestRpcMessage msg) {
        ClusterAPIProtos.PluginAddress address = msg.getAddress();
        UserId pluginTenantId = new UserId(toUUID(address.getTenantId()));
        PluginId pluginId = new PluginId(toUUID(address.getPluginId()));

        //TenantId deviceTenantId = new TenantId(toUUID(msg.getDeviceTenantId()));
        DeviceId deviceId = new DeviceId(toUUID(msg.getDeviceId()));

        ToDeviceRpcRequestBody requestBody = new ToDeviceRpcRequestBody(msg.getMethod(), msg.getParams());
        ToDeviceRpcRequest request = new ToDeviceRpcRequest(toUUID(msg.getMsgId()), deviceId, msg.getOneway(), msg.getExpTime(), requestBody);

        return new ToDeviceRpcRequestPluginMsg(serverAddress, pluginId, pluginTenantId, request);
    }
*/

    @Override
    public void onFromDeviceRpcResponseRpcMsg(GrpcSession session, ClusterAPIProtos.ToPluginRpcResponseRpcMessage msg) {
        log.trace("{} session [{}] received session actors msg {}", getType(session), session.getRemoteServer(), msg);
        service.onMsg(deserialize(session.getRemoteServer(), msg));
    }

    @Override
    public void onToAllNodesRpcMessage(GrpcSession session, ClusterAPIProtos.ToAllNodesRpcMessage msg) {
        log.trace("{} session [{}] received session actors msg {}", getType(session), session.getRemoteServer(), msg);
        service.onMsg((ToAllNodesMsg) deserialize(msg.getData().toByteArray()));
    }

    @Override
    public void onError(GrpcSession session, Throwable t) {
        log.warn("{} session got error -> {}", getType(session), session.getRemoteServer(), t);
        manager.tell(new RpcSessionClosedMsg(session.isClient(), session.getRemoteServer()), self);
        session.close();
    }

    private static String getType(GrpcSession session) {
        return session.isClient() ? "Client" : "Server";
    }

    private static PluginRpcMsg convert(ServerAddress serverAddress, ClusterAPIProtos.ToPluginRpcMessage msg) {
        ClusterAPIProtos.PluginAddress address = msg.getAddress();
        //TenantId userId = new TenantId(toUUID(address.getUserId()));
        UserId tenantId = new UserId(toUUID(address.getTenantId()));

        PluginId pluginId = new PluginId(toUUID(address.getPluginId()));
        RpcMsg rpcMsg = new RpcMsg(serverAddress, msg.getClazz(), msg.getData().toByteArray());
        return new PluginRpcMsg(tenantId,pluginId, rpcMsg);
    }

    private static UUID toUUID(ClusterAPIProtos.Uid uid) {
        return new UUID(uid.getPluginUuidMsb(), uid.getPluginUuidLsb());
    }


    private static ToPluginRpcResponseDeviceMsg deserialize(ServerAddress serverAddress, ClusterAPIProtos.ToPluginRpcResponseRpcMessage msg) {
        ClusterAPIProtos.PluginAddress address = msg.getAddress();
        UserId pluginTenantId = new UserId(toUUID(address.getTenantId()));
        PluginId pluginId = new PluginId(toUUID(address.getPluginId()));

        RpcError error = !StringUtils.isEmpty(msg.getError()) ? RpcError.valueOf(msg.getError()) : null;
        return new ToPluginRpcResponseDeviceMsg(pluginId, pluginTenantId);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T deserialize(byte[] data) {
        return (T) SerializationUtils.deserialize(data);
    }

}
