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
package tb.rulegin.server.actors.rule;


import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.msg.plugin.torule.PluginToRuleMsg;
import tb.rulegin.server.common.data.id.RuleId;
import tb.rulegin.server.actors.msg.cluster.ClusterEventMsg;
import tb.rulegin.server.actors.msg.cluster.ComponentLifecycleMsg;
import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.actors.service.ComponentActor;
import tb.rulegin.server.actors.service.ContextBasedCreator;
import tb.rulegin.server.actors.stats.StatsPersistTick;
import tb.rulegin.server.actors.msg.core.RuleToPluginTimeoutMsg;
import tb.rulegin.server.core.process.RuleActorMessageProcessor;

public class RuleActor extends ComponentActor<RuleId, RuleActorMessageProcessor> {

    private RuleActor(ActorSystemContext systemContext, RuleId ruleId, UserId userId) {
        super(systemContext, ruleId);
        setProcessor(new RuleActorMessageProcessor( ruleId,userId, systemContext, logger));
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        logger.info("Rule Actor received message[{}] : {}", id, msg);
        if (msg instanceof RuleProcessingMsg) {
            try {
                processor.onRuleProcessingMsg(context(), (RuleProcessingMsg) msg);
                increaseMessagesProcessedCount();
            } catch (Exception e) {
                logAndPersist("onDeviceMsg", e);
            }
        } else if (msg instanceof PluginToRuleMsg<?>) {
            try {

                processor.onPluginMsg(context(), (PluginToRuleMsg<?>) msg);
            } catch (Exception e) {
                logAndPersist("onPluginMsg", e);
            }
        } else if (msg instanceof ComponentLifecycleMsg) {
            // 实现的是 RuleActorMessageProcessor
            onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
        } else if (msg instanceof ClusterEventMsg) {
            onClusterEventMsg((ClusterEventMsg) msg);
        } else if (msg instanceof RuleToPluginTimeoutMsg) {
            try {
                processor.onTimeoutMsg(context(), (RuleToPluginTimeoutMsg) msg);
            } catch (Exception e) {
                logAndPersist("onTimeoutMsg", e);
            }
        } else if (msg instanceof StatsPersistTick) {
            onStatsPersistTick(id);
        } else {
            logger.info("[{}][{}] Unknown msg type.", id, msg.getClass().getName());
        }
    }

    public static class ActorCreator extends ContextBasedCreator<RuleActor> {
        private static final long serialVersionUID = 1L;
        private final UserId userId;
        private final RuleId ruleId;

        public ActorCreator(ActorSystemContext context, RuleId ruleId, UserId userId) {
            super(context);
            this.ruleId = ruleId;
            this.userId = userId;
        }

        @Override
        public RuleActor create() throws Exception {
            return new RuleActor(context, ruleId,userId);
        }
    }

    @Override
    protected long getErrorPersistFrequency() {
        return systemContext.getRuleErrorPersistFrequency();
    }
}
