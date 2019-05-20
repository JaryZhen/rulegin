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
package com.github.rulegin.dao.plugin;

import com.github.rulegin.common.data.UUIDConverter;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.plugin.PluginMetaData;
import com.github.rulegin.dao.DaoUtil;
import com.github.rulegin.dao.JpaAbstractSearchTextDao;
import com.github.rulegin.dao.model.PluginMetaDataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by Valerii Sosliuk on 5/1/2017.
 */
@Slf4j
@Component
public class JpaBasePluginDao extends JpaAbstractSearchTextDao<PluginMetaDataEntity, PluginMetaData> implements PluginDao {

    @Autowired
    private PluginMetaDataRepository pluginMetaDataRepository;

    @Override
    protected Class<PluginMetaDataEntity> getEntityClass() {
        return PluginMetaDataEntity.class;
    }

    @Override
    protected CrudRepository<PluginMetaDataEntity, String> getCrudRepository() {
        return pluginMetaDataRepository;
    }

    @Override
    public List<PluginMetaData> findSystemPlugin() {

        List<PluginMetaDataEntity> entities = new ArrayList<>();
        Iterable<PluginMetaDataEntity> a = pluginMetaDataRepository.findAll();
        for (PluginMetaDataEntity r : a){
            entities.add(r);
        }
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}]", Arrays.toString(entities.toArray()));
        } else {
            log.debug("Search result: [{}]", entities.size());
        }

        return DaoUtil.convertDataList(entities);
    }


    @Override
    public PluginMetaData findById(PluginId pluginId) {
        log.debug("Search plugin meta-dataSource entity by id [{}]", pluginId);
        PluginMetaData pluginMetaData = super.findById(pluginId.getId());
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}] for plugin entity [{}]", pluginMetaData != null, pluginMetaData);
        } else {
            log.debug("Search result: [{}]", pluginMetaData != null);
        }
        return pluginMetaData;
    }

    @Override
    public PluginMetaData findByApiToken(String apiToken) {
        log.debug("Search plugin meta-dataSource entity by api token [{}]", apiToken);
        PluginMetaDataEntity entity = pluginMetaDataRepository.findByApiToken(apiToken);
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}] for plugin entity [{}]", entity != null, entity);
        } else {
            log.debug("Search result: [{}]", entity != null);
        }
        return DaoUtil.getData(entity);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Delete plugin meta-dataSource entity by id [{}]", id);
        pluginMetaDataRepository.delete(UUIDConverter. fromTimeUUID(id));
    }

    @Override
    public void deleteById(PluginId pluginId) {
        deleteById(pluginId.getId());
    }

    @Override
    public PluginMetaData findByClazz(String clazz){
        PluginMetaDataEntity entities = pluginMetaDataRepository.findByClazz(clazz);
        return DaoUtil.getData(entities);
    }
 /*   @Override
    public List<PluginMetaData> findByTenantIdAndPageLink(TenantId userId, TextPageLink pageLink) {
        log.debug("Try to find plugins by userId [{}] and pageLink [{}]", userId, pageLink);
        List<PluginMetaDataEntity> entities = pluginMetaDataRepository
                .findByTenantIdAndPageLink(
                        UUIDConverter.fromTimeUUID(userId.getId()),
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? NULL_UUID_STR :  UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        new PageRequest(0, pageLink.getLimit()));
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}]", Arrays.toString(entities.toArray()));
        } else {
            log.debug("Search result: [{}]", entities.size());
        }
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public List<PluginMetaData> findAllTenantPluginsByTenantId(UUID userId, TextPageLink pageLink) {
        log.debug("Try to find all user plugins by userId [{}] and pageLink [{}]", userId, pageLink);
        List<PluginMetaDataEntity> entities = pluginMetaDataRepository
                .findAllTenantPluginsByTenantId(
                        UUIDConverter.fromTimeUUID(userId),
                        NULL_UUID_STR,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? NULL_UUID_STR :  UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        new PageRequest(0, pageLink.getLimit()));
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}]", Arrays.toString(entities.toArray()));
        } else {
            log.debug("Search result: [{}]", entities.size());
        }
        return DaoUtil.convertDataList(entities);
    }

    */
}
