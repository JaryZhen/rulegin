
package tb.rulegin.server.actors.service;


import tb.rulegin.server.actors.service.cluster.discovery.DiscoveryServiceListener;
import tb.rulegin.server.actors.service.cluster.rpc.RpcMsgListener;
import tb.rulegin.server.common.data.component.ComponentLifecycleEvent;
import tb.rulegin.server.common.data.id.DeviceId;
import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.id.RuleId;
import tb.rulegin.server.actors.session.SessionMsgProcessor;
import tb.rulegin.server.common.data.id.UserId;

public interface ActorService extends SessionMsgProcessor, WebSocketMsgProcessor
        , RestMsgProcessor, RpcMsgListener, DiscoveryServiceListener {


    void onPluginStateChange(UserId tenantId, PluginId pluginId, ComponentLifecycleEvent state);

    void onRuleStateChange(UserId tenantId, RuleId ruleId, ComponentLifecycleEvent state);

    void onCredentialsUpdate(UserId tenantId, DeviceId deviceId);

    void onDeviceNameOrTypeUpdate(UserId tenantId, DeviceId deviceId, String deviceName, String deviceType);
}
