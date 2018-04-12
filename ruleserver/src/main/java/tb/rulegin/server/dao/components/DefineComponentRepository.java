package tb.rulegin.server.dao.components;

import tb.rulegin.server.common.data.component.ComponentScope;
import tb.rulegin.server.common.data.component.ComponentType;
import tb.rulegin.server.dao.model.DefineComponentEntity;
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
