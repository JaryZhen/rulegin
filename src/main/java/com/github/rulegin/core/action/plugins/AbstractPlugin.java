
package com.github.rulegin.core.action.plugins;


import com.github.rulegin.actors.msg.core.RpcMsg;
import com.github.rulegin.actors.msg.core.TimeoutMsg;
import com.github.rulegin.actors.msg.plugin.aware.PluginRestMsg;
import com.github.rulegin.actors.msg.plugin.aware.PluginWebsocketMsg;
import com.github.rulegin.actors.msg.rule.RuleToPluginMsg;
import com.github.rulegin.actors.service.cluster.ServerAddress;
import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.core.rule.RuleException;
import com.github.rulegin.core.handlers.rest.DefaultRestMsgHandler;
import com.github.rulegin.core.handlers.rest.RestMsgHandler;
import com.github.rulegin.core.handlers.rpc.DefaultRpcMsgHandler;
import com.github.rulegin.core.handlers.rpc.RpcMsgHandler;
import com.github.rulegin.core.handlers.ws.DefaultWebsocketMsgHandler;
import com.github.rulegin.core.handlers.ws.WebsocketMsgHandler;
import com.github.rulegin.core.handlers.rule.DefaultRuleMsgHandler;
import com.github.rulegin.core.handlers.rule.RuleMsgHandler;

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
