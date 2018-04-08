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
package cn.neucloud.server.core.action.plugins;


import cn.neucloud.server.actors.service.cluster.ServerAddress;
import cn.neucloud.server.common.component.ConfigurableComponent;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.actors.msg.core.TimeoutMsg;
import cn.neucloud.server.actors.msg.plugin.aware.PluginWebsocketMsg;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.actors.msg.plugin.aware.PluginRestMsg;
import cn.neucloud.server.actors.msg.core.RpcMsg;
import cn.neucloud.server.core.rule.RuleException;

public interface Plugin<T> extends ConfigurableComponent<T> {

    void process(PluginContext ctx, PluginWebsocketMsg<?> wsMsg);

    void process(PluginContext ctx, UserId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException;

    void process(PluginContext ctx, PluginRestMsg msg);

    void process(PluginContext ctx, RpcMsg msg);

    void process(PluginContext ctx, TimeoutMsg<?> msg);

    void onServerAdded(PluginContext ctx, ServerAddress server);

    void onServerRemoved(PluginContext ctx, ServerAddress server);

    void resume(PluginContext ctx);

    void suspend(PluginContext ctx);

    void stop(PluginContext ctx);

}
