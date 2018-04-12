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
package tb.rulegin.server.core.action.plugins;


import tb.rulegin.server.common.data.id.EntityId;
import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.id.UserId;

import java.io.Serializable;

public final class PluginApiCallSecurityContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private final PluginId pluginId;
    private final UserId userId;

    public PluginApiCallSecurityContext(PluginId pluginId, UserId userId) {
        super();
        this.pluginId = pluginId;
        this.userId = userId;
    }

    public UserId getPluginTenantId(){
        return this.userId;
    }

    public PluginId getPluginId() {
        return pluginId;
    }

    public boolean isSystemAdmin() {
        return userId == null || EntityId.NULL_UUID.equals(userId.getId());
    }

    public boolean isTenantAdmin() {
        return !isSystemAdmin() && (userId == null || EntityId.NULL_UUID.equals(userId.getId()));
    }

    public boolean isCustomerUser() {
        return !isSystemAdmin() && !isTenantAdmin();
    }

    public UserId getCustomerId() {
        return userId;
    }

}
