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
package com.github.rulegin.actors.rule;


import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.service.DefaultActorService;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.common.data.page.PageDataIterable;
import com.github.rulegin.common.data.rule.RuleMetaData;
import com.github.rulegin.dao.model.ModelConstants;

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
