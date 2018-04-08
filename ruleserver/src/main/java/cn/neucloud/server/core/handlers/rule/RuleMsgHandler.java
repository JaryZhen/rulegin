package cn.neucloud.server.core.handlers.rule;


import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.core.action.plugins.PluginContext;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.core.rule.RuleException;
import cn.neucloud.server.common.data.id.RuleId;


public interface RuleMsgHandler {

    void process(PluginContext ctx, UserId userId,RuleId ruleId,  RuleToPluginMsg<?> msg) throws RuleException;
    //void process(PluginContext ctx, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;

}
