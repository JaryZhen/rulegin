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
package com.github.rulegin.dao.components;

import com.github.rulegin.common.data.component.ComponentScope;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.component.DefineComponent;
import com.github.rulegin.common.data.id.ComponentDescriptorId;
import com.github.rulegin.common.data.page.TextPageData;
import com.github.rulegin.common.data.page.TextPageLink;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Andrew Shvayka
 */
public interface DefineComponentService {

    DefineComponent saveComponent(DefineComponent component);

    DefineComponent findById(ComponentDescriptorId componentId);

    DefineComponent findByClazz(String clazz);

    DefineComponent findByDataSourceTypeAndFiltersType(String data,String filter);

    TextPageData<DefineComponent> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink);

    TextPageData<DefineComponent> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink);

    boolean validate(DefineComponent component, JsonNode configuration);

    void deleteByClazz(String clazz);

}
