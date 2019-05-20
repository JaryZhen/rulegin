
package com.github.rulegin.dao.component;


import com.github.rulegin.common.data.component.ComponentDescriptor;
import com.github.rulegin.common.data.component.ComponentScope;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.id.ComponentDescriptorId;
import com.github.rulegin.common.data.page.TextPageLink;
import com.github.rulegin.dao.Dao;

import java.util.List;
import java.util.Optional;

/**
 */
public interface ComponentDescriptorDao extends Dao<ComponentDescriptor> {

    Optional<ComponentDescriptor> saveIfNotExist(ComponentDescriptor component);

    ComponentDescriptor findById(ComponentDescriptorId componentId);

    ComponentDescriptor findByClazz(String clazz);

    List<ComponentDescriptor> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink);

    List<ComponentDescriptor> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink);

    void deleteById(ComponentDescriptorId componentId);

    void deleteByClazz(String clazz);

}
