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

import cn.neucloud.server.common.component.ConfigurableComponent;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.core.rule.RuleContext;
import cn.neucloud.server.core.rule.RuleLifecycleComponent;
import cn.neucloud.server.core.rule.RuleProcessingMetaData;

import java.util.Optional;

public interface PluginAction<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    Optional<RuleToPluginMsg<?>> convert(RuleContext ctx,  RuleProcessingMetaData deviceMsgMd);
    boolean isOneWayAction();

}
