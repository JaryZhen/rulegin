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


import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.plugin.PluginMetaData;
import tb.rulegin.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface PluginDao extends Dao<PluginMetaData> {

    PluginMetaData save(PluginMetaData plugin);

    List<PluginMetaData> findSystemPlugin();
    PluginMetaData findById(PluginId pluginId);

    PluginMetaData findByApiToken(String apiToken);
    PluginMetaData findByClazz(String clazz);

    void deleteById(UUID id);

    void deleteById(PluginId pluginId);

    //List<PluginMetaData> findByTenantIdAndPageLink( TextPageLink pageLink);

    /**
     * Find all user plugins (including system) by userId and page link.
     *
     *   userId the userId
     * @param pageLink the page link
     * @return the list of plugins objects
     */
    //List<PluginMetaData> findAllTenantPluginsByTenantId(TextPageLink pageLink);

}
