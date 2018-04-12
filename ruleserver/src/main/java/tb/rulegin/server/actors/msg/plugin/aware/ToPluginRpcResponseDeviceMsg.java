
package tb.rulegin.server.actors.msg.plugin.aware;

import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.id.UserId;
import lombok.Data;


@Data
public class ToPluginRpcResponseDeviceMsg implements ToPluginActorMsg {
    private final PluginId pluginId;
    private final UserId pluginTenantId;
}

