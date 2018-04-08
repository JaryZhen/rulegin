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
package cn.neucloud.server.common.data.rule;

import cn.neucloud.server.common.data.HasName;
import cn.neucloud.server.common.data.SearchTextBased;
import cn.neucloud.server.common.data.component.ComponentLifecycleState;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RuleMetaData extends SearchTextBased<RuleId> implements HasName {

    private static final long serialVersionUID = -5656679015122935465L;

    private UserId userId;
    private String name;
    private ComponentLifecycleState state;
    private int weight;

    private JsonNode dataSource;
    private JsonNode filters;
    private JsonNode actions;

    public RuleMetaData() {
        super();
    }

    public RuleMetaData(RuleId id) {
        super(id);
    }

    public RuleMetaData(RuleMetaData rule) {
        super(rule);

        this.userId = rule.userId;
        this.name = rule.getName();
        this.state = rule.getState();
        this.weight = rule.getWeight();

        this.dataSource = rule.getDataSource();
        this.filters = rule.getFilters();
        this.actions = rule.getActions();
    }

    @Override
    public String getSearchText() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

}
