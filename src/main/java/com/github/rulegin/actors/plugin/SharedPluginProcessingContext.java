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
package com.github.rulegin.actors.plugin;

import akka.actor.ActorRef;
import com.github.rulegin.actors.msg.core.TimeoutMsg;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.service.cluster.routing.ClusterRoutingService;
import com.github.rulegin.actors.service.cluster.rpc.ClusterRpcService;
import com.github.rulegin.common.data.id.DeviceId;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.actors.service.cluster.ServerAddress;
import com.github.rulegin.core.handlers.plugin.PluginWebSocketMsgEndpoint;
import com.github.rulegin.dao.plugin.PluginService;
import com.github.rulegin.dao.rule.RuleService;
import com.github.rulegin.dao.timeseries.TimeseriesService;
import com.github.rulegin.dao.user.UserService;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Slf4j
public final class SharedPluginProcessingContext {
    final ActorRef parentActor;
    final ActorRef currentActor;
    final ActorSystemContext systemContext;
    final PluginWebSocketMsgEndpoint msgEndpoint;
   // final AssetService assetService;

    final RuleService ruleService;
    final PluginService pluginService;
    final UserService customerService;
   // final TenantService tenantService;
    final TimeseriesService tsService;
   // final AttributesService attributesService;
    final ClusterRpcService rpcService;
    final ClusterRoutingService routingService;
    final PluginId pluginId;
    final UserId tenantId;

    public SharedPluginProcessingContext(ActorSystemContext sysContext, PluginId pluginId,
                                         ActorRef parentActor, ActorRef self, UserId tenantId) {
        super();
        //this.userId = userId;
        this.pluginId = pluginId;
        this.parentActor = parentActor;
        this.currentActor = self;
        this.systemContext = sysContext;
        this.msgEndpoint = sysContext.getWsMsgEndpoint();
        this.tsService = sysContext.getTsService();
       // this.attributesService = sysContext.getAttributesService();
        //this.assetService = sysContext.getAssetService();

        this.rpcService = sysContext.getRpcService();
        this.routingService = sysContext.getRoutingService();
        this.ruleService = sysContext.getRuleService();
        this.pluginService = sysContext.getPluginService();
        this.customerService = sysContext.getCustomerService();
        //this.tenantService = sysContext.getTenantService();
        this.tenantId = tenantId;
    }

    public PluginId getPluginId() {
        return pluginId;
    }

    public UserId getPluginTenantId() {
        return tenantId;
    }

    //public void toDeviceActor(DeviceAttributesEventNotificationMsg msg) {
    //    forward(msg.getDeviceId(), msg, rpcService::tell);
    //}

/*    public void sendRpcRequest(ToDeviceRpcRequest msg) {
        log.trace("[{}] Forwarding msg {} to device actors!", pluginId, msg);
        ToDeviceRpcRequestPluginMsg rpcMsg = new ToDeviceRpcRequestPluginMsg(pluginId,userId, msg);
        forward(msg.getDeviceId(), rpcMsg, rpcService::tell);
    }*/

    private <T> void forward(DeviceId deviceId, T msg, BiConsumer<ServerAddress, T> rpcFunction) {
        Optional<ServerAddress> instance = routingService.resolveById(deviceId);
        if (instance.isPresent()) {
            log.trace("[{}] Forwarding msg {} to remote device actors!", pluginId, msg);
            rpcFunction.accept(instance.get(), msg);
        } else {
            log.trace("[{}] Forwarding msg {} to local device actors!", pluginId, msg);
            parentActor.tell(msg, ActorRef.noSender());
        }
    }

    public void scheduleTimeoutMsg(TimeoutMsg msg) {
        log.debug("Scheduling msg {} with delay {} ms", msg, msg.getTimeout());
        systemContext.getScheduler().scheduleOnce(
                Duration.create(msg.getTimeout(), TimeUnit.MILLISECONDS),
                currentActor,
                msg,
                systemContext.getActorSystem().dispatcher(),
                ActorRef.noSender());

    }

    public void persistError(String method, Exception e) {
        systemContext.persistError( pluginId, method, e);
    }

    public ActorRef self() {
        return currentActor;
    }
}
