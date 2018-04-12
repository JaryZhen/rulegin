/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tb.rulegin.server.actors.rule;


import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.actors.service.DefaultActorService;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.common.data.page.PageDataIterable;
import tb.rulegin.server.common.data.rule.RuleMetaData;
import tb.rulegin.server.dao.model.ModelConstants;

public class SystemRuleManager extends RuleManager {

    public SystemRuleManager(ActorSystemContext systemContext) {
        super(systemContext, new UserId(ModelConstants.NULL_UUID));
    }

    @Override
    PageDataIterable.FetchFunction<RuleMetaData> getFetchRulesFunction() {
        return ruleService::findSystemRules;
    }

    @Override
    String getDispatcherName() {
        return DefaultActorService.SYSTEM_RULE_DISPATCHER_NAME;
    }
}
