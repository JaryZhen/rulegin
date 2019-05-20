package com.github.rulegin.core.handlers.rest;

import com.github.rulegin.actors.msg.plugin.aware.PluginRestMsg;
import com.github.rulegin.core.action.plugins.PluginContext;

public interface RestMsgHandler {

    void process(PluginContext ctx, PluginRestMsg msg);

}
