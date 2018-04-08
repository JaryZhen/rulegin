
package cn.neucloud.server.actors.service;


import cn.neucloud.server.actors.msg.plugin.aware.PluginRestMsg;

public interface RestMsgProcessor {

    void process(PluginRestMsg msg);

}
