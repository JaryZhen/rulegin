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
package cn.neucloud.server.core.handlers.rule;

import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.core.action.plugins.PluginContext;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.core.rule.RuleException;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.actors.msg.MsgType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Andrew Shvayka
 */
@Slf4j
public class DefaultRuleMsgHandler implements RuleMsgHandler {

    @Override
    public void process(PluginContext ctx, UserId userId, RuleId ruleId, RuleToPluginMsg<?> msg) throws RuleException {
       /* if (msg instanceof TelemetryUploadRequestRuleToPluginMsg) {
            handleTelemetryUploadRequest(ctx, userId, ruleId, (TelemetryUploadRequestRuleToPluginMsg) msg);
        } else if (msg instanceof UpdateAttributesRequestRuleToPluginMsg) {
            handleUpdateAttributesRequest(ctx,  userId, ruleId, (UpdateAttributesRequestRuleToPluginMsg) msg);
        } else if (msg instanceof GetAttributesRequestRuleToPluginMsg) {
            handleGetAttributesRequest(ctx,  userId, ruleId, (GetAttributesRequestRuleToPluginMsg) msg);
        }*/
        //TODO: handle subscriptions to attribute updates.
    }

/*    protected void handleGetAttributesRequest(PluginContext ctx, UserId userId, RuleId ruleId, GetAttributesRequestRuleToPluginMsg msg) {
        msgTypeNotSupported(msg.getPayload().getMsgType());
    }

    protected void handleUpdateAttributesRequest(PluginContext ctx, UserId userId,RuleId ruleId, UpdateAttributesRequestRuleToPluginMsg msg) {
        msgTypeNotSupported(msg.getPayload().getMsgType());
    }

    protected void handleTelemetryUploadRequest(PluginContext ctx, UserId userId,RuleId ruleId, TelemetryUploadRequestRuleToPluginMsg msg) {
        msgTypeNotSupported(msg.getPayload().getMsgType());
    }
*/
    private void msgTypeNotSupported(MsgType msgType) {
        throw new RuntimeException("Not supported msg type: " + msgType + "!");
    }

}
