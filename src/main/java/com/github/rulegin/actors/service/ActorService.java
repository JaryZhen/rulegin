
package com.github.rulegin.actors.service;


import com.github.rulegin.actors.service.cluster.discovery.DiscoveryServiceListener;
import com.github.rulegin.actors.service.cluster.rpc.RpcMsgListener;
import com.github.rulegin.common.data.component.ComponentLifecycleEvent;
import com.github.rulegin.common.data.id.DeviceId;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.actors.session.SessionMsgProcessor;
import com.github.rulegin.common.data.id.UserId;

public interface ActorService extends SessionMsgProcessor, WebSocketMsgProcessor
        , RestMsgProcessor, RpcMsgListener, DiscoveryServiceListener {


    void onPluginStateChange(UserId tenantId, PluginId pluginId, ComponentLifecycleEvent state);

    void onRuleStateChange(UserId tenantId, RuleId ruleId, ComponentLifecycleEvent state);

    void onCredentialsUpdate(UserId tenantId, DeviceId deviceId);

    void onDeviceNameOrTypeUpdate(UserId tenantId, DeviceId deviceId, String deviceName, String deviceType);
}
