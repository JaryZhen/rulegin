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
package cn.neucloud.server.actors.msg.plugin.torule;


import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.common.data.id.RuleId;

import java.io.Serializable;
import java.util.UUID;

/**
 * The basic interface for messages that are sent from particular plugin to Rule
 * instance
 * 
 * @author ashvayka
 * @see RuleToPluginMsg
 *
 */
public interface PluginToRuleMsg<V extends Serializable> extends ToRuleActorMsg ,  Serializable {

    /**
     * Returns the unique identifier of the message
     * 
     * @return unique identifier of the message.
     */
    UUID getUid();

    /**
     * Returns the unique identifier of the user that owns the Rule
     *
     * @return unique identifier of the user that owns the Rule.
     *
     */
    UserId getUserId();

    /**
     * Returns the unique identifier of the Rule
     * 
     * @return unique identifier of the Rule.
     */
    RuleId getRuleId();

    /**
     * Returns the serializable message payload.
     * 
     * @return the serializable message payload.
     */
    V getPayload();

}
