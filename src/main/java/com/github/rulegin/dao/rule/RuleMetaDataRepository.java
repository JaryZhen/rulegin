package com.github.rulegin.dao.rule;

import com.github.rulegin.dao.model.RuleMetaDataEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Valerii Sosliuk on 4/30/2017.
 */
public interface RuleMetaDataRepository extends CrudRepository<RuleMetaDataEntity, String> {

    List<RuleMetaDataEntity> findByName(String name);

    /*
        @Query("SELECT rmd FROM RuleMetaDataEntity rmd WHERE rmd.userId = :userId " +
                "AND LOWER(rmd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
                "AND rmd.id > :idOffset ORDER BY rmd.id")
        List<RuleMetaDataEntity> findByTenantIdAndPageLink(@Param("userId") String userId,
                                                           @Param("textSearch") String textSearch,
                                                           @Param("idOffset") String idOffset,
                                                           Pageable pageable);

        @Query("SELECT rmd FROM RuleMetaDataEntity rmd WHERE rmd.userId IN (:userId, :nullTenantId) " +
                "AND LOWER(rmd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
                "AND rmd.id > :idOffset ORDER BY rmd.id")
        List<RuleMetaDataEntity> findAllTenantRulesByTenantId(@Param("userId") String userId,
                                                              @Param("nullTenantId") String nullTenantId,
                                                              @Param("textSearch") String textSearch,
                                                              @Param("idOffset") String idOffset,
                                                              Pageable pageable);

                                                              */

/*    @Override
    RuleMetaDataEntity find(String s);*/

}
