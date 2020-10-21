package com.github.rulegin.dao.components;

import com.github.rulegin.common.data.UUIDConverter;
import com.github.rulegin.common.data.component.ComponentScope;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.component.DefineComponent;
import com.github.rulegin.common.data.id.ComponentDescriptorId;
import com.github.rulegin.common.data.page.TextPageLink;
import com.github.rulegin.dao.DaoUtil;
import com.github.rulegin.dao.JpaAbstractSearchTextDao;
import com.github.rulegin.dao.model.DefineComponentEntity;
import com.github.rulegin.dao.model.ModelConstants;
import com.github.rulegin.dao.util.UUIDs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
public class JpaBaseDefineComponentDao extends JpaAbstractSearchTextDao<DefineComponentEntity, DefineComponent>
        implements DefineComponentDao {

    @Autowired
    private DefineComponentRepository comRepository;

    @Override
    protected Class<DefineComponentEntity> getEntityClass() {
        return DefineComponentEntity.class;
    }

    @Override
    protected CrudRepository<DefineComponentEntity, String> getCrudRepository() {
        return comRepository;
    }

    @Override
    public Optional<DefineComponent> saveIfNotExist(DefineComponent component) {
        if (component.getId() == null) {
            component.setId(new ComponentDescriptorId(UUIDs.timeBased()));
        }
        if (comRepository.findById(UUIDConverter.fromTimeUUID(component.getId().getId())).orElse(null) == null) {
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
        return DaoUtil.getData(comRepository.findByClazz(clazz));
    }

    @Override
    public DefineComponent findByDataSourceTypeAndFiltersType(String data, String filter) {
        return DaoUtil.getData(comRepository.findByDataSourceTypeAndFiltersType(data,filter));
    }

    @Override
    public List<DefineComponent> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink) {
        return DaoUtil.convertDataList(comRepository
                .findByType(
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? ModelConstants.NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        PageRequest.of(0, pageLink.getLimit())));
    }

    @Override
    public List<DefineComponent> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink) {
        return DaoUtil.convertDataList(comRepository
                .findByScopeAndType(
                        type,
                        scope,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getIdOffset() == null ? ModelConstants.NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                        PageRequest.of(0, pageLink.getLimit())));
    }

    @Override
    @Transactional
    public void deleteById(ComponentDescriptorId componentId) {
        removeById(componentId.getId());
    }

    @Override
    @Transactional
    public void deleteByClazz(String clazz) {
        comRepository.deleteByClazz(clazz);
    }
}
