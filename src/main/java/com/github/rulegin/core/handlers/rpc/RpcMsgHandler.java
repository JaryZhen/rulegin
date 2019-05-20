package com.github.rulegin.core.handlers.rpc;

import com.github.rulegin.actors.msg.core.RpcMsg;
import com.github.rulegin.core.action.plugins.PluginContext;

public interface RpcMsgHandler {

    void process(PluginContext ctx, RpcMsg msg);

}
