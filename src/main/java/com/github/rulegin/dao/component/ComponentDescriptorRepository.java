package com.github.rulegin.dao.component;

import com.github.rulegin.common.data.component.ComponentScope;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.dao.model.ComponentDescriptorEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 */
public interface ComponentDescriptorRepository extends CrudRepository<ComponentDescriptorEntity, String> {

    ComponentDescriptorEntity findByClazz(String clazz);

    @Query("SELECT cd FROM ComponentDescriptorEntity cd WHERE cd.type = :type " +
            "AND LOWER(cd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
            "AND cd.id > :idOffset ORDER BY cd.id")
    List<ComponentDescriptorEntity> findByType(@Param("type") ComponentType type,
                                               @Param("textSearch") String textSearch,
                                               @Param("idOffset") String idOffset,
                                               Pageable pageable);

    @Query("SELECT cd FROM ComponentDescriptorEntity cd WHERE cd.type = :type " +
            "AND cd.scope = :scope AND LOWER(cd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
            "AND cd.id > :idOffset ORDER BY cd.id")
    List<ComponentDescriptorEntity> findByScopeAndType(@Param("type") ComponentType type,
                                                       @Param("scope") ComponentScope scope,
                                                       @Param("textSearch") String textSearch,
                                                       @Param("idOffset") String idOffset,
                                                       Pageable pageable);

    void deleteByClazz(String clazz);
}
