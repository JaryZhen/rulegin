
package com.github.rulegin.actors.msg.plugin.aware;

import com.github.rulegin.actors.msg.core.RpcMsg;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.id.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class PluginRpcMsg implements ToPluginActorMsg {

    private final UserId tenantId;
    private final PluginId pluginId;
    @Getter
    private final RpcMsg rpcMsg;

    @Override
    public UserId getPluginTenantId() {
        return tenantId;
    }

    @Override
    public PluginId getPluginId() {
        return pluginId;
    }



}
