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
package cn.neucloud.server.dao.model;

import cn.neucloud.server.common.data.component.DefineComponent;
import cn.neucloud.server.common.data.component.ComponentScope;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.id.ComponentDescriptorId;
import cn.neucloud.server.dao.util.mapping.JsonStringType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.COMPONENT_DESCRIPTOR_COLUMN_FAMILY_NAME2)
public class DefineComponentEntity extends BaseSqlEntity<DefineComponent> implements SearchTextEntity<DefineComponent> {

    @Transient
    private static final long serialVersionUID = 253590350877882402L;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_TYPE_PROPERTY)
    private ComponentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_SCOPE_PROPERTY)
    private ComponentScope scope;

    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_DATASOURCE_PROPERTY)
    private String dataSourceType;

    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_FILTERS_PROPERTY)
    private String filtersType;

    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_ACTIONS_PROPERTY)
    private String actionsType;

    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_NAME_PROPERTY)
    private String name;

    @Column(name = ModelConstants.COMPONENT_DESCRIPTOR_CLASS_PROPERTY)
    private String clazz;


    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    public DefineComponentEntity() {
    }

    public DefineComponentEntity(DefineComponent component) {
        if (component.getId() != null) {
            this.setId(component.getId().getId());
        }
        this.dataSourceType = component.getDataSourceType();
        this.filtersType = component.getFiltersType();
        this.actionsType = component.getActionsType();
        this.type = component.getType();
        this.scope = component.getScope();
        this.name = component.getName();
        this.clazz = component.getClazz();
        this.searchText = component.getName();
    }

    @Override
    public DefineComponent toData() {
        DefineComponent data = new DefineComponent(new ComponentDescriptorId(this.getId()));
        data.setType(this.getType());
        data.setScope(this.getScope());
        data.setName(this.getName());
        data.setClazz(this.getClazz());
        data.setDataSourceType(this.getDataSourceType());
        data.setFiltersType(this.getFiltersType());
        data.setActionsType(this.getActionsType());
        return data;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public String getSearchTextSource() {
        return searchText;
    }
}
