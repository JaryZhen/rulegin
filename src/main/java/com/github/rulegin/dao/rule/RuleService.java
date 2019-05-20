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
package com.github.rulegin.dao.rule;

import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.common.data.page.TextPageData;
import com.github.rulegin.common.data.page.TextPageLink;
import com.github.rulegin.common.data.rule.RuleMetaData;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public interface RuleService {

    RuleMetaData saveRule(RuleMetaData device);

    List<RuleMetaData> findAll();

    RuleMetaData findRuleById(RuleId ruleId);

    ListenableFuture<RuleMetaData> findRuleByIdAsync(RuleId ruleId);

    List<RuleMetaData> findPluginRules(String pluginToken);

    TextPageData<RuleMetaData> findSystemRules(TextPageLink pageLink);

    TextPageData<RuleMetaData> findTenantRules(UserId tenantId, TextPageLink pageLink);

    List<RuleMetaData> findSystemRules();

    //TextPageData<RuleMetaData> findAllTenantRulesByTenantIdAndPageLink(TenantId userId, TextPageLink pageLink);

    //List<RuleMetaData> findAllTenantRulesByTenantId(TenantId userId);

    void activateRuleById(RuleId ruleId);

    void suspendRuleById(RuleId ruleId);

    void deleteRuleById(RuleId ruleId);

    List<RuleMetaData> findAllUserRulesByUserId(UserId tenantId);

    //void deleteRulesByTenantId(TenantId userId);

}
