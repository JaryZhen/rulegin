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
        ruleMetaDataRepository.deleteById(UUIDConverter.fromTimeUUID(id));
    }

    @Override
    public void deleteById(RuleId ruleId) {
        deleteById(ruleId.getId());
    }
}
