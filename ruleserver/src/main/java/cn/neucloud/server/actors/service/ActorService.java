
package cn.neucloud.server.actors.service;


import cn.neucloud.server.actors.service.cluster.discovery.DiscoveryServiceListener;
import cn.neucloud.server.actors.service.cluster.rpc.RpcMsgListener;
import cn.neucloud.server.common.data.component.ComponentLifecycleEvent;
import cn.neucloud.server.common.data.id.DeviceId;
import cn.neucloud.server.common.data.id.PluginId;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.actors.session.SessionMsgProcessor;
import cn.neucloud.server.common.data.id.UserId;

public interface ActorService extends SessionMsgProcessor, WebSocketMsgProcessor
        , RestMsgProcessor, RpcMsgListener, DiscoveryServiceListener {


    void onPluginStateChange(UserId tenantId, PluginId pluginId, ComponentLifecycleEvent state);

    void onRuleStateChange(UserId tenantId, RuleId ruleId, ComponentLifecycleEvent state);

    void onCredentialsUpdate(UserId tenantId, DeviceId deviceId);

    void onDeviceNameOrTypeUpdate(UserId tenantId, DeviceId deviceId, String deviceName, String deviceType);
}
