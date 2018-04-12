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
package tb.rulegin.server.actors.msg.wrapper;


import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.msg.rule.RuleToPluginMsg;
import tb.rulegin.server.actors.msg.plugin.aware.PluginAwareMsg;
import tb.rulegin.server.common.data.id.PluginId;
import tb.rulegin.server.common.data.id.RuleId;

public class RuleToPluginMsgWrapper implements PluginAwareMsg, RuleAwareMsg ,UserAwareMsg{

    private final PluginId pluginId;
    private final RuleId ruleId;
    private final RuleToPluginMsg<?> msg;
    private final UserId userId;

    public RuleToPluginMsgWrapper(PluginId pluginId, RuleId ruleId, RuleToPluginMsg<?> msg, UserId userId) {
        super();
        this.pluginId = pluginId;
        this.ruleId = ruleId;
        this.msg = msg;
        this.userId = userId;
    }


    @Override
    public PluginId getPluginId() {
        return pluginId;
    }


    @Override
    public RuleId getRuleId() {
        return ruleId;
    }


    public RuleToPluginMsg<?> getMsg() {
        return msg;
    }

    @Override
    public UserId getUserId() {
        return userId;
    }
}
