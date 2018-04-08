package cn.neucloud.server.core.handlers.rpc;

import cn.neucloud.server.actors.msg.core.RpcMsg;
import cn.neucloud.server.core.action.plugins.PluginContext;

public interface RpcMsgHandler {

    void process(PluginContext ctx, RpcMsg msg);

}
