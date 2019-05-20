package com.github.rulegin.actors.rule;


import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.service.DefaultActorService;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.common.data.page.PageDataIterable;
import com.github.rulegin.common.data.rule.RuleMetaData;

public class UserRuleManager extends RuleManager {

    public UserRuleManager(ActorSystemContext systemContext, UserId userId) {
        super(systemContext, userId);
    }

    @Override
    PageDataIterable.FetchFunction<RuleMetaData> getFetchRulesFunction() {
        return link ->
                ruleService.findTenantRules(userId, link);
    }

    @Override
    String getDispatcherName() {
        return DefaultActorService.TENANT_RULE_DISPATCHER_NAME;
    }

}
