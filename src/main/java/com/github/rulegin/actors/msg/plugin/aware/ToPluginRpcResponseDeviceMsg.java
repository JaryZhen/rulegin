
package com.github.rulegin.actors.msg.plugin.aware;

import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.id.UserId;
import lombok.Data;


@Data
public class ToPluginRpcResponseDeviceMsg implements ToPluginActorMsg {
    private final PluginId pluginId;
    private final UserId pluginTenantId;
}

