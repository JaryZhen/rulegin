
package tb.rulegin.server.actors.service;


import tb.rulegin.server.actors.msg.plugin.aware.PluginRestMsg;

public interface RestMsgProcessor {

    void process(PluginRestMsg msg);

}
