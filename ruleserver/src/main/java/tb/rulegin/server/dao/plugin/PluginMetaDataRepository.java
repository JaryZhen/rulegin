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
package tb.rulegin.server.dao.plugin;

import tb.rulegin.server.dao.model.PluginMetaDataEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Valerii Sosliuk on 5/1/2017.
 */

public interface PluginMetaDataRepository extends CrudRepository<PluginMetaDataEntity, String> {

    PluginMetaDataEntity findByApiToken(String apiToken);

    PluginMetaDataEntity  findByClazz(String clazz);

 /*   @Query("SELECT pmd FROM PluginMetaDataEntity pmd WHERE pmd.userId = :userId " +
            "AND LOWER(pmd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
            "AND pmd.id > :idOffset ORDER BY pmd.id")
    List<PluginMetaDataEntity> findByTenantIdAndPageLink(@Param("userId") String userId,
                                                         @Param("textSearch") String textSearch,
                                                         @Param("idOffset") String idOffset,
                                                         Pageable pageable);

    @Query("SELECT pmd FROM PluginMetaDataEntity pmd WHERE pmd.userId IN (:userId, :nullTenantId) " +
            "AND LOWER(pmd.searchText) LIKE LOWER(CONCAT(:textSearch, '%')) " +
            "AND pmd.id > :idOffset ORDER BY pmd.id")
    List<PluginMetaDataEntity> findAllTenantPluginsByTenantId(@Param("userId") String userId,
                                                              @Param("nullTenantId") String nullTenantId,
                                                              @Param("textSearch") String textSearch,
                                                              @Param("idOffset") String idOffset,
                                                              Pageable pageable);*/
}
