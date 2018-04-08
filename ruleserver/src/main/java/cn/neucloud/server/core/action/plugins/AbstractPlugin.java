
package cn.neucloud.server.core.action.plugins;


import cn.neucloud.server.actors.service.cluster.ServerAddress;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.actors.msg.core.TimeoutMsg;
import cn.neucloud.server.actors.msg.plugin.aware.PluginWebsocketMsg;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.actors.msg.plugin.aware.PluginRestMsg;
import cn.neucloud.server.actors.msg.core.RpcMsg;
import cn.neucloud.server.core.handlers.rest.DefaultRestMsgHandler;
import cn.neucloud.server.core.handlers.rest.RestMsgHandler;
import cn.neucloud.server.core.handlers.rpc.DefaultRpcMsgHandler;
import cn.neucloud.server.core.handlers.rpc.RpcMsgHandler;
import cn.neucloud.server.core.handlers.ws.DefaultWebsocketMsgHandler;
import cn.neucloud.server.core.handlers.ws.WebsocketMsgHandler;
import cn.neucloud.server.core.rule.RuleException;
import cn.neucloud.server.core.handlers.rule.DefaultRuleMsgHandler;
import cn.neucloud.server.core.handlers.rule.RuleMsgHandler;

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
