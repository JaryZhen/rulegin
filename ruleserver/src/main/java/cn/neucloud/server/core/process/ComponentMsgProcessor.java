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
package cn.neucloud.server.core.process;

import akka.actor.ActorContext;
import akka.event.LoggingAdapter;
import cn.neucloud.server.actors.ActorSystemContext;
import cn.neucloud.server.actors.stats.StatsPersistTick;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.actors.msg.cluster.ClusterEventMsg;

public abstract class ComponentMsgProcessor<T> extends AbstractContextAwareMsgProcessor {

    protected final UserId userId;

    protected final T entityId;

    protected ComponentMsgProcessor(ActorSystemContext systemContext, LoggingAdapter logger, UserId userId, T id) {
        super(systemContext, logger);
        this.userId = userId;
        this.entityId = id;
    }

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    public abstract void onCreated(ActorContext context) throws Exception;

    public abstract void onUpdate(ActorContext context) throws Exception;

    public abstract void onActivate(ActorContext context) throws Exception;

    public abstract void onSuspend(ActorContext context) throws Exception;

    public abstract void onStop(ActorContext context) throws Exception;

    public abstract void onClusterEventMsg(ClusterEventMsg msg) throws Exception;

    public void scheduleStatsPersistTick(ActorContext context, long statsPersistFrequency) {
        schedulePeriodicMsgWithDelay(context, new StatsPersistTick(), statsPersistFrequency, statsPersistFrequency);
    }
}
