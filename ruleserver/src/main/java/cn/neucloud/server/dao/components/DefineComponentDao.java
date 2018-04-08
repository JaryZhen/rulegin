
package cn.neucloud.server.dao.components;


import cn.neucloud.server.common.data.component.DefineComponent;
import cn.neucloud.server.common.data.component.ComponentScope;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.id.ComponentDescriptorId;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.dao.Dao;

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
