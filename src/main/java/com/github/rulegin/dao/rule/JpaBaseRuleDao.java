/**
 * Copyright © 2016-2017 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rulegin.dao.rule;

import com.github.rulegin.common.data.UUIDConverter;
import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.common.data.rule.RuleMetaData;
import com.github.rulegin.dao.DaoUtil;
import com.github.rulegin.dao.JpaAbstractSearchTextDao;
import com.github.rulegin.dao.model.RuleMetaDataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
@Slf4j
@Component
public class JpaBaseRuleDao extends JpaAbstractSearchTextDao<RuleMetaDataEntity, RuleMetaData> implements RuleDao {

    @Autowired
    private RuleMetaDataRepository ruleMetaDataRepository;

    @Override
    protected Class<RuleMetaDataEntity> getEntityClass() {
        return RuleMetaDataEntity.class;
    }

    @Override
    protected CrudRepository<RuleMetaDataEntity, String> getCrudRepository() {
        return ruleMetaDataRepository;
    }

    @Override
    public RuleMetaData findById(RuleId ruleId) {
        return findById(ruleId.getId());
    }

    @Override
    public List<RuleMetaData> findRulesByPlugin(String pluginToken) {
        log.debug("Search rules by api token [{}]", pluginToken);
        return DaoUtil.convertDataList(ruleMetaDataRepository.findByName(pluginToken));
    }

    @Override
    public List<RuleMetaData> findSystemRules() {

        List<RuleMetaDataEntity> entities = new ArrayList<>();
        Iterable<RuleMetaDataEntity> a = ruleMetaDataRepository.findAll();
        for (RuleMetaDataEntity r : a){
            entities.add(r);
        }
        if (log.isTraceEnabled()) {
            log.trace("Search result: [{}]", Arrays.toString(entities.toArray()));
        } else {
            log.debug("Search result: [{}]", entities.size());
        }

        return DaoUtil.convertDataList(entities);
    }
/*
    @Override
    public List<RuleMetaData> findByTenantIdAndPageLink(TenantId userId, TextPageLink pageLink) {
        log.debug("Try to find rules by userId [{}] and pageLink [{}]", userId, pageLink);
        List<RuleMetaDataEntity> entities =
                ruleMetaDataRepository
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
    public List<RuleMetaData> findAllTenantRulesByTenantId(UUID userId, TextPageLink pageLink) {
        log.debug("Try to find all user rules by userId [{}] and pageLink [{}]", userId, pageLink);
        List<RuleMetaDataEntity> entities =
                ruleMetaDataRepository
                        .findAllTenantRulesByTenantId(
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

    @Override
    public void deleteById(UUID id) {
        log.debug("Delete Rule meta-dataSource entity by id [{}]", id);
        ruleMetaDataRepository.delete(UUIDConverter.fromTimeUUID(id));
    }

    @Override
    public void deleteById(RuleId ruleId) {
        deleteById(ruleId.getId());
    }
}
