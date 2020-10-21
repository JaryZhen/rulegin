package com.github.rulegin.dao.components;

import com.github.rulegin.common.data.component.ComponentScope;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.dao.model.DefineComponentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DefineComponentRepository extends CrudRepository<DefineComponentEntity, String> {

    DefineComponentEntity findByClazz(String clazz);

    @Query("SELECT cd FROM DefineComponentEntity cd WHERE cd.type = :type " +
            "AND LOWER(cd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
            "AND cd.id > :idOffset ORDER BY cd.id")
    List<DefineComponentEntity> findByType(@Param("type") ComponentType type,
                                           @Param("textSearch") String textSearch,
                                           @Param("idOffset") String idOffset,
                                           Pageable pageable);

    @Query("SELECT cd FROM DefineComponentEntity cd WHERE cd.type = :type " +
            "AND cd.scope = :scope AND LOWER(cd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
            "AND cd.id > :idOffset ORDER BY cd.id")
    List<DefineComponentEntity> findByScopeAndType(@Param("type") ComponentType type,
                                                   @Param("scope") ComponentScope scope,
                                                   @Param("textSearch") String textSearch,
                                                   @Param("idOffset") String idOffset,
                                                   Pageable pageable);

    void deleteByClazz(String clazz);

    @Query("SELECT cd FROM DefineComponentEntity cd WHERE cd.dataSourceType = :dataSourceType AND cd.filtersType = :filtersType")
    DefineComponentEntity findByDataSourceTypeAndFiltersType(@Param("dataSourceType") String dataSourceType,
                                                             @Param("filtersType") String filtersType);
}
