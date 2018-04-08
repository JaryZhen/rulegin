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
package cn.neucloud.server.dao.components;

import cn.neucloud.server.common.data.UUIDConverter;
import cn.neucloud.server.common.data.component.DefineComponent;
import cn.neucloud.server.common.data.component.ComponentScope;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.id.ComponentDescriptorId;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.dao.DaoUtil;
import cn.neucloud.server.dao.JpaAbstractSearchTextDao;
import cn.neucloud.server.dao.model.DefineComponentEntity;
import cn.neucloud.server.dao.util.UUIDs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.neucloud.server.dao.model.ModelConstants.NULL_UUID_STR;


@Component
public class JpaBaseDefineComponentDao extends JpaAbstractSearchTextDao<DefineComponentEntity, DefineComponent>
        implements DefineComponentDao {

    @Autowired
    private DefineComponentRepository repository;

    @Override
    protected Class<DefineComponentEntity> getEntityClass() {
        return DefineComponentEntity.class;
    }

    @Override
    protected CrudRepository<DefineComponentEntity, String> getCrudRepository() {
        return repository;
    }

    @Override
    public Optional<DefineComponent> saveIfNotExist(DefineComponent component) {
        if (component.getId() == null) {
            component.setId(new ComponentDescriptorId(UUIDs.timeBased()));
        }
        if (repository.findOne(UUIDConverter.fromTimeUUID(component.getId().getId())) == null) {
            return Optional.of(save(component));
        }
        return Optional.empty();
    }

    @Override
    public DefineComponent findById(ComponentDescriptorId componentId) {
        return findById(componentId.getId());
    }

    @Override
    public DefineComponent findByClazz(String clazz) {
        return DaoUtil.getData(repository.findByClazz(clazz));
    }

    @Override
    public DefineComponent findByDataSourceTypeAndFiltersType(String data, String filter) {
        return DaoUtil.getData(repository.findByDataSourceTypeAndFiltersType(data,filter));
    }

    @Override
    public List<DefineComponent> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink) {
        return DaoUtil.convertDataList(repository
                .findByType(
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        new PageRequest(0, pageLink.getLimit())));
    }

    @Override
    public List<DefineComponent> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink) {
        return DaoUtil.convertDataList(repository
                .findByScopeAndType(
                        type,
                        scope,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        new PageRequest(0, pageLink.getLimit())));
    }

    @Override
    @Transactional
    public void deleteById(ComponentDescriptorId componentId) {
        removeById(componentId.getId());
    }

    @Override
    @Transactional
    public void deleteByClazz(String clazz) {
        repository.deleteByClazz(clazz);
    }
}
