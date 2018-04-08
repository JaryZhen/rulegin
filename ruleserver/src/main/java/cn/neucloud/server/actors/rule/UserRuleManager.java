package cn.neucloud.server.actors.rule;


import cn.neucloud.server.actors.ActorSystemContext;
import cn.neucloud.server.actors.service.DefaultActorService;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.common.data.page.PageDataIterable;
import cn.neucloud.server.common.data.rule.RuleMetaData;

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
