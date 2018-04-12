package tb.rulegin.server.core.handlers.rule;


import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.core.action.plugins.PluginContext;
import tb.rulegin.server.actors.msg.rule.RuleToPluginMsg;
import tb.rulegin.server.core.rule.RuleException;
import tb.rulegin.server.common.data.id.RuleId;


public interface RuleMsgHandler {

    void process(PluginContext ctx, UserId userId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;
    //void process(PluginContext ctx, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;

}
