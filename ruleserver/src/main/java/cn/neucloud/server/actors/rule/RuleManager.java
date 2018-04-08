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
package cn.neucloud.server.actors.rule;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import cn.neucloud.server.common.data.component.ComponentLifecycleEvent;
import cn.neucloud.server.common.data.component.ComponentLifecycleState;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.common.data.page.PageDataIterable;
import cn.neucloud.server.common.data.rule.RuleMetaData;
import cn.neucloud.server.actors.ActorSystemContext;
import cn.neucloud.server.actors.ContextAwareActor;
import cn.neucloud.server.dao.rule.RuleService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public abstract class RuleManager {

    protected final ActorSystemContext systemContext;
    protected final RuleService ruleService;
    protected final Map<RuleId, ActorRef> ruleActors;
    protected final UserId userId;

    Map<RuleMetaData, RuleActorMetaData> ruleMap = new HashMap<>();
    private RuleActorChain ruleChain;

    public RuleManager(ActorSystemContext systemContext, UserId tenantId) {
        this.systemContext = systemContext;
        this.ruleService = systemContext.getRuleService();
        this.userId = tenantId;
        this.ruleActors = new HashMap<>();
    }

    public void init(ActorContext context) {
        // find system rules and add ruleActor for each
        PageDataIterable<RuleMetaData> ruleIterator = new PageDataIterable<>(getFetchRulesFunction(),
                ContextAwareActor.ENTITY_PACK_LIMIT);
        ruleMap = new HashMap<>();

        //为系统已有的rule创建对应的actor
        for (RuleMetaData rule : ruleIterator) {
            log.debug("[{}] Creating Rule actors {}", rule.getId(), rule);
            ActorRef ref = getOrCreateRuleActor(context, rule.getId());
            RuleActorMetaData actorMd = RuleActorMetaData.systemRule(rule.getId(), rule.getWeight(), ref);
            ruleMap.put(rule, actorMd);
            log.info("[{}] Rule actors created.", rule.getId());
        }

        // activated Rule
        refreshRuleChain();

        // process(context, new RuleChainDeviceMsg())

    }

    /*    void process(ActorContext context, RuleChainDeviceMsg srcMsg) {
            ChainProcessingMetaData md = new ChainProcessingMetaData(srcMsg.getRuleChain(),
                    srcMsg.getToDeviceActorMsg(), new DeviceMetaData(deviceId, deviceName, deviceType, deviceAttributes), context.self());
            ChainProcessingContext ctx = new ChainProcessingContext(md);
            if (ctx.getChainLength() > 0) {
                RuleProcessingMsg msg = new RuleProcessingMsg(ctx);
                ActorRef ruleActorRef = ctx.getCurrentActor();
                ruleActorRef.tell(msg, ActorRef.noSender());
            } else {
                context.self().tell(new RulesProcessedMsg(ctx), context.self());
            }
        }*/

    public Optional<ActorRef> update(ActorContext context, RuleId ruleId, ComponentLifecycleEvent event) {
        RuleMetaData rule;
        if (event != ComponentLifecycleEvent.DELETED) {
            rule = systemContext.getRuleService().findRuleById(ruleId);
        } else {
            rule = ruleMap.keySet().stream()
                    .filter(r -> r.getId().equals(ruleId))
                    .peek(r -> r.setState(ComponentLifecycleState.SUSPENDED))
                    .findFirst()
                    .orElse(null);
            if (rule != null) {
                ruleMap.remove(rule);
                ruleActors.remove(ruleId);
            }
        }
        if (rule != null) {
            RuleActorMetaData actorMd = ruleMap.get(rule);
            if (actorMd == null) {
                ActorRef ref = getOrCreateRuleActor(context, rule.getId());
                actorMd = RuleActorMetaData.systemRule(rule.getId(), rule.getWeight(), ref);
                ruleMap.put(rule, actorMd);
            }
            //相同的rule actor 只保留一个
            refreshRuleChain();
            //which RuleActor???
            return Optional.of(actorMd.getActorRef());
        } else {
            log.warn("[{}] Can't process unknown Rule!", ruleId);
            return Optional.empty();
        }
    }

    abstract PageDataIterable.FetchFunction<RuleMetaData> getFetchRulesFunction();

    abstract String getDispatcherName();

    public ActorRef getOrCreateRuleActor(ActorContext context, RuleId ruleId) {
        return ruleActors.computeIfAbsent(ruleId, rId ->
                context.actorOf(Props.create(new RuleActor.ActorCreator(systemContext, rId, userId))
                        .withDispatcher(getDispatcherName()), rId.toString()));
    }

    public RuleActorChain getRuleChain() {
        return ruleChain;
    }


    private void refreshRuleChain() {
        Set<RuleActorMetaData> activeRuleSet = new HashSet<>();
        for (Map.Entry<RuleMetaData, RuleActorMetaData> rule : ruleMap.entrySet()) {
            if (rule.getKey().getState() == ComponentLifecycleState.ACTIVE) {
                activeRuleSet.add(rule.getValue());
            }
        }
        ruleChain = new SimpleRuleActorChain(activeRuleSet);
    }


}
