
package tb.rulegin.server.core.action.plugins;


import tb.rulegin.server.actors.service.cluster.ServerAddress;
import tb.rulegin.server.common.data.id.RuleId;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.msg.core.TimeoutMsg;
import tb.rulegin.server.actors.msg.plugin.aware.PluginWebsocketMsg;
import tb.rulegin.server.actors.msg.rule.RuleToPluginMsg;
import tb.rulegin.server.actors.msg.plugin.aware.PluginRestMsg;
import tb.rulegin.server.actors.msg.core.RpcMsg;
import tb.rulegin.server.core.handlers.rest.DefaultRestMsgHandler;
import tb.rulegin.server.core.handlers.rest.RestMsgHandler;
import tb.rulegin.server.core.handlers.rpc.DefaultRpcMsgHandler;
import tb.rulegin.server.core.handlers.rpc.RpcMsgHandler;
import tb.rulegin.server.core.handlers.ws.DefaultWebsocketMsgHandler;
import tb.rulegin.server.core.handlers.ws.WebsocketMsgHandler;
import tb.rulegin.server.core.rule.RuleException;
import tb.rulegin.server.core.handlers.rule.DefaultRuleMsgHandler;
import tb.rulegin.server.core.handlers.rule.RuleMsgHandler;

public abstract class AbstractPlugin<T> implements Plugin<T> {

    @Override
    public void process(PluginContext ctx, PluginWebsocketMsg<?> wsMsg) {
        getWebsocketMsgHandler().process(ctx, wsMsg);
    }

    @Override
    public void process(PluginContext ctx, PluginRestMsg msg) {
        getRestMsgHandler().process(ctx, msg);
    }

    @Override
    public void process(PluginContext ctx, UserId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException {
        getRuleMsgHandler().process(ctx, tenantId,ruleId,msg);
    }

    @Override
    public void process(PluginContext ctx, RpcMsg msg) {
        getRpcMsgHandler().process(ctx, msg);
    }


    @Override
    public void process(PluginContext ctx, TimeoutMsg<?> msg) {
        throw new IllegalStateException("Timeouts is not handled in current plugin!");
    }

    @Override
    public void onServerAdded(PluginContext ctx, ServerAddress server) {
    }

    @Override
    public void onServerRemoved(PluginContext ctx, ServerAddress server) {
    }

    protected RuleMsgHandler getRuleMsgHandler() {
        return new DefaultRuleMsgHandler();
    }

    protected RestMsgHandler getRestMsgHandler() {
        return new DefaultRestMsgHandler();
    }

    protected WebsocketMsgHandler getWebsocketMsgHandler() {
        return new DefaultWebsocketMsgHandler();
    }

    protected RpcMsgHandler getRpcMsgHandler() {
        return new DefaultRpcMsgHandler();
    }
}
