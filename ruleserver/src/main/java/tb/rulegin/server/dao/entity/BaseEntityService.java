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
package tb.rulegin.server.dao.entity;

import tb.rulegin.server.common.data.HasName;
import tb.rulegin.server.common.data.id.EntityId;
import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.id.RuleId;
import tb.rulegin.server.dao.plugin.PluginService;
import tb.rulegin.server.dao.rule.RuleService;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ashvayka on 04.05.17.
 */
@Service
@Slf4j
public class BaseEntityService extends AbstractEntityService implements EntityService {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private PluginService pluginService;


    @Override
    public void deleteEntityRelations(EntityId entityId) {
        super.deleteEntityRelations(entityId);
    }

    @Override
    public ListenableFuture<String> fetchEntityNameAsync(EntityId entityId) {
        log.trace("Executing fetchEntityNameAsync [{}]", entityId);
        ListenableFuture<String> entityName;
        ListenableFuture<? extends HasName> hasName;
        switch (entityId.getEntityType()) {

            case RULE:
                hasName = ruleService.findRuleByIdAsync(new RuleId(entityId.getId()));
                break;
            case PLUGIN:
                hasName = pluginService.findPluginByIdAsync(new PluginId(entityId.getId()));
                break;
            default:
                throw new IllegalStateException("Not Implemented!");
        }
        entityName = Futures.transform(hasName, (Function<HasName, String>) hasName1 -> hasName1 != null ? hasName1.getName() : null );
        return entityName;
    }

}
