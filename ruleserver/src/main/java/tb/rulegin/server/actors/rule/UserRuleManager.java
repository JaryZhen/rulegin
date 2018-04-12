package tb.rulegin.server.actors.rule;


import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.actors.service.DefaultActorService;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.common.data.page.PageDataIterable;
import tb.rulegin.server.common.data.rule.RuleMetaData;

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
