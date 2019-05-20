
package com.github.rulegin.dao.components;


import com.github.rulegin.common.data.component.ComponentScope;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.component.DefineComponent;
import com.github.rulegin.common.data.id.ComponentDescriptorId;
import com.github.rulegin.common.data.page.TextPageLink;
import com.github.rulegin.dao.Dao;

import java.util.List;
import java.util.Optional;

/**
 */
public interface DefineComponentDao extends Dao<DefineComponent> {

    Optional<DefineComponent> saveIfNotExist(DefineComponent component);

    DefineComponent findById(ComponentDescriptorId componentId);

    DefineComponent findByClazz(String clazz);
    DefineComponent findByDataSourceTypeAndFiltersType(String data, String filter);

    List<DefineComponent> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink);

    List<DefineComponent> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink);

    void deleteById(ComponentDescriptorId componentId);

    void deleteByClazz(String clazz);

}
