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
package com.github.rulegin.dao.model;

import com.github.rulegin.common.data.component.ComponentLifecycleState;
import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.common.data.rule.RuleMetaData;
import com.github.rulegin.dao.DaoUtil;
import com.github.rulegin.dao.util.mapping.JsonStringType;
import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.RULE_COLUMN_FAMILY_NAME)
public class RuleMetaDataEntity extends BaseSqlEntity<RuleMetaData> implements SearchTextEntity<RuleMetaData> {

    @Transient
    private static final long serialVersionUID = -1506905644259463884L;

    @Column(name = ModelConstants.RULE_USER_ID_PROPERTY)
    private String userId;

    @Column(name = ModelConstants.RULE_NAME_PROPERTY)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.RULE_STATE_PROPERTY)
    private ComponentLifecycleState state;

    @Column(name = ModelConstants.RULE_WEIGHT_PROPERTY)
    private int weight;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;


    @Type(type = "json")
    @Column(name = ModelConstants.RULE_DATASOURCE,length = 500)
    private JsonNode dataSource;

    @Type(type = "json")
    @Column(name = ModelConstants.RULE_FILTERS)
    private JsonNode filters;

    @Type(type = "json")
    @Column(name = ModelConstants.RULE_ACTION)
    private JsonNode action;


    public RuleMetaDataEntity() {
    }

    public RuleMetaDataEntity(RuleMetaData rule) {
        if (rule.getId() != null) {
            this.setId(rule.getUuidId());
        }
        this.userId = toString(DaoUtil.getId(rule.getUserId()));
        this.name = rule.getName();
        this.state = rule.getState();
        this.weight = rule.getWeight();
        this.searchText = rule.getName();

        this.dataSource = rule.getDataSource();
        this.filters = rule.getFilters();
        this.action = rule.getActions();
    }

    @Override
    public String getSearchTextSource() {
        return searchText;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public RuleMetaData toData() {
        RuleMetaData rule = new RuleMetaData(new RuleId(getId()));
        rule.setUserId(new UserId(toUUID(userId)));
        rule.setName(name);
        rule.setState(state);
        rule.setWeight(weight);

        rule.setDataSource(dataSource);
        rule.setFilters(filters);
        rule.setActions(action);

        rule.setCreatedTime(UUIDs.unixTimestamp(getId()));


        return rule;
    }
}
