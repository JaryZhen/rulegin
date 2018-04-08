
package cn.neucloud.server.dao.component;


import cn.neucloud.server.common.data.component.ComponentDescriptor;
import cn.neucloud.server.common.data.component.ComponentScope;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.id.ComponentDescriptorId;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.dao.Dao;

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
