
package tb.rulegin.server.dao.component;


import tb.rulegin.server.common.data.component.ComponentDescriptor;
import tb.rulegin.server.common.data.component.ComponentScope;
import tb.rulegin.server.common.data.component.ComponentType;
import tb.rulegin.server.common.data.id.ComponentDescriptorId;
import tb.rulegin.server.common.data.page.TextPageLink;
import tb.rulegin.server.dao.Dao;

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
