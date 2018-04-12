
package tb.rulegin.server.actors.msg.plugin.aware;

import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.msg.core.RpcMsg;
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
