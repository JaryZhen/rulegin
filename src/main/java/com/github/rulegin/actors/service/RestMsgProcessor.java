
package com.github.rulegin.actors.service;


import com.github.rulegin.actors.msg.plugin.aware.PluginRestMsg;

public interface RestMsgProcessor {

    void process(PluginRestMsg msg);

}
