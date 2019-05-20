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

    @Override
    RuleMetaDataEntity findOne(String s);

}
