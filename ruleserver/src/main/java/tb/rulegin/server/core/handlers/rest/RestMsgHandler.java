package tb.rulegin.server.core.handlers.rest;

import tb.rulegin.server.actors.msg.plugin.aware.PluginRestMsg;
import tb.rulegin.server.core.action.plugins.PluginContext;

public interface RestMsgHandler {

    void process(PluginContext ctx, PluginRestMsg msg);

}
