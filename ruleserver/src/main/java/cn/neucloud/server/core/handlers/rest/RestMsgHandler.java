package cn.neucloud.server.core.handlers.rest;

import cn.neucloud.server.actors.msg.plugin.aware.PluginRestMsg;
import cn.neucloud.server.core.action.plugins.PluginContext;

public interface RestMsgHandler {

    void process(PluginContext ctx, PluginRestMsg msg);

}
