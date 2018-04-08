
package cn.neucloud.server.actors.msg.plugin.aware;

import cn.neucloud.server.common.data.id.PluginId;
import cn.neucloud.server.common.data.id.UserId;
import lombok.Data;


@Data
public class ToPluginRpcResponseDeviceMsg implements ToPluginActorMsg {
    private final PluginId pluginId;
    private final UserId pluginTenantId;
}

