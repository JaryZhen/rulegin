package com.github.rulegin.core.handlers.rule;


import com.github.rulegin.actors.msg.rule.RuleToPluginMsg;
import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.core.action.plugins.PluginContext;
import com.github.rulegin.core.rule.RuleException;


public interface RuleMsgHandler {

    void process(PluginContext ctx, UserId userId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;
    //void process(PluginContext ctx, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;

}
