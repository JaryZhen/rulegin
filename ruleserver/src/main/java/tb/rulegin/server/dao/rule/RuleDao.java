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
package tb.rulegin.server.dao.rule;


import tb.rulegin.server.common.data.id.RuleId;
import tb.rulegin.server.common.data.rule.RuleMetaData;
import tb.rulegin.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface RuleDao extends Dao<RuleMetaData> {

    RuleMetaData save(RuleMetaData rule);

    RuleMetaData findById(RuleId ruleId);

    List<RuleMetaData> findRulesByPlugin(String pluginToken);
    List<RuleMetaData> findSystemRules();
    //List<RuleMetaData> findByTenantIdAndPageLink( TextPageLink pageLink);
    //
    ///**
    // * Find all user rules (including system) by userId and page link.
    // *
    // * @param userId the userId
    // * @param pageLink the page link
    // * @return the list of rules objects
    // */
    //List<RuleMetaData> findAllTenantRulesByTenantId(UUID userId, TextPageLink pageLink);

    void deleteById(UUID id);

    void deleteById(RuleId ruleId);
}
