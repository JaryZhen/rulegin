package tb.rulegin.server.core.handlers.rpc;

import tb.rulegin.server.actors.msg.core.RpcMsg;
import tb.rulegin.server.core.action.plugins.PluginContext;

public interface RpcMsgHandler {

    void process(PluginContext ctx, RpcMsg msg);

}
