
package com.github.rulegin.actors.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import com.github.rulegin.actors.msg.cluster.ClusterEventMsg;
import com.github.rulegin.actors.msg.cluster.ComponentLifecycleMsg;
import com.github.rulegin.actors.msg.cluster.ToAllNodesMsg;
import com.github.rulegin.actors.msg.core.RpcBroadcastMsg;
import com.github.rulegin.actors.msg.core.RpcSessionCreateRequestMsg;
import com.github.rulegin.actors.msg.core.TimeoutMsg;
import com.github.rulegin.actors.msg.plugin.aware.PluginRestMsg;
import com.github.rulegin.actors.msg.plugin.aware.PluginWebsocketMsg;
import com.github.rulegin.actors.msg.plugin.aware.ToPluginActorMsg;
import com.github.rulegin.actors.msg.session.SessionAwareMsg;
import com.github.rulegin.actors.rpc.RpcManagerActor;
import com.github.rulegin.actors.stats.StatsActor;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.app.AppActor;
import com.github.rulegin.actors.service.cluster.discovery.DiscoveryService;
import com.github.rulegin.actors.service.cluster.discovery.ServerInstance;
import com.github.rulegin.actors.service.cluster.rpc.ClusterRpcService;
import com.github.rulegin.actors.session.SessionManagerActor;
import com.github.rulegin.common.data.component.ComponentLifecycleEvent;
import com.github.rulegin.common.data.id.DeviceId;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.id.RuleId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class DefaultActorService implements ActorService {

    private static final String ACTOR_SYSTEM_NAME = "Akka";

    public static final String APP_DISPATCHER_NAME = "app-dispatcher";
    public static final String CORE_DISPATCHER_NAME = "core-dispatcher";
    public static final String SYSTEM_RULE_DISPATCHER_NAME = "system-rule-dispatcher";
    public static final String SYSTEM_PLUGIN_DISPATCHER_NAME = "system-plugin-dispatcher";
    public static final String TENANT_RULE_DISPATCHER_NAME = "rule-dispatcher";
    public static final String TENANT_PLUGIN_DISPATCHER_NAME = "plugin-dispatcher";
    public static final String SESSION_DISPATCHER_NAME = "session-dispatcher";
    public static final String RPC_DISPATCHER_NAME = "rpc-dispatcher";

    @Autowired
    private ActorSystemContext actorContext;

    @Autowired
    private ClusterRpcService rpcService;

    @Autowired
    private DiscoveryService discoveryService;

    private ActorSystem system;

    private ActorRef appActor;

    private ActorRef sessionManagerActor;

    private ActorRef rpcManagerActor;

    @PostConstruct
    public void initActorSystem() {
        log.info("java Initializing..."+this.getClass().getSimpleName());

        log.info("Initialize Actor system {}", actorContext.getRuleService().getClass().getSimpleName());
        actorContext.setActorService(this);
        system = ActorSystem.create(ACTOR_SYSTEM_NAME, actorContext.getConfig());
        actorContext.setActorSystem(system);

        appActor = system.actorOf(Props.create(new AppActor.ActorCreator(actorContext)).withDispatcher(APP_DISPATCHER_NAME), "appActor");
        actorContext.setAppActor(appActor);

        sessionManagerActor = system.actorOf(Props.create(new SessionManagerActor.ActorCreator(actorContext)).withDispatcher(CORE_DISPATCHER_NAME), "sessionManagerActor");
        actorContext.setSessionManagerActor(sessionManagerActor);

        rpcManagerActor = system.actorOf(Props.create(new RpcManagerActor.ActorCreator(actorContext)).withDispatcher(CORE_DISPATCHER_NAME), "rpcManagerActor");
        ActorRef statsActor = system.actorOf(Props.create(new StatsActor.ActorCreator(actorContext)).withDispatcher(CORE_DISPATCHER_NAME), "statsActor");
        actorContext.setStatsActor(statsActor);

        rpcService.init(this);

        discoveryService.addListener(this);
        log.info("Actor system initialized.");
    }


    @PreDestroy
    public void stopActorSystem() {
        Future<Terminated> status = system.terminate();
        try {
            Terminated terminated = Await.result(status, Duration.Inf());
            log.info("Actor system terminated: {}", terminated);
        } catch (Exception e) {
            log.error("Failed to terminate actors system.", e);
        }
    }

    @Override
    public void process(SessionAwareMsg msg) {
        if (msg instanceof SessionAwareMsg) {
            log.debug("Processing session aware msg: {}", msg);
            sessionManagerActor.tell(msg, ActorRef.noSender());
        }
    }

    @Override
    public void process(PluginWebsocketMsg<?> msg) {
        log.debug("Processing websocket msg: {}", msg);
        appActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void process(PluginRestMsg msg) {
        log.debug("Processing rest msg: {}", msg);
        appActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void onMsg(ToPluginActorMsg msg) {
        log.info("Processing plugin rpc msg: {}", msg);
        appActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void onMsg(ToAllNodesMsg msg) {
        log.info("Processing broadcast rpc msg: {}", msg);
        appActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void onMsg(RpcSessionCreateRequestMsg msg) {
        log.info("Processing session create msg: {}", msg);
        rpcManagerActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void onMsg(TimeoutMsg.RpcSessionTellMsg msg) {
        log.info("Processing session rpc msg: {}", msg);
        rpcManagerActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void onMsg(RpcBroadcastMsg msg) {
        log.info("Processing broadcast rpc msg: {}", msg);
        rpcManagerActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public void onServerAdded(ServerInstance server) {
        log.info("Processing onServerAdded msg: {}", server);
        broadcast(new ClusterEventMsg(server.getServerAddress(), true));
    }

    @Override
    public void onServerUpdated(ServerInstance server) {

    }

    @Override
    public void onServerRemoved(ServerInstance server) {
        log.info("Processing onServerRemoved msg: {}", server);
        broadcast(new ClusterEventMsg(server.getServerAddress(), false));
    }

    @Override
    public void onPluginStateChange(UserId tenantId, PluginId pluginId, ComponentLifecycleEvent state) {
        log.info("[{}] Processing onPluginStateChange event: {}", pluginId, state);
        broadcast(ComponentLifecycleMsg.forPlugin(pluginId,tenantId,  state));
    }

    @Override
    public void onRuleStateChange(UserId userId, RuleId ruleId, ComponentLifecycleEvent state) {
        log.info("[{}] Processing onRuleStateChange event: {}", ruleId, state);
        //forRule 有ruleID 没有pluginId
        broadcast(ComponentLifecycleMsg.forRule(ruleId,userId, state));
    }

    @Override
    public void onCredentialsUpdate(UserId tenantId, DeviceId deviceId) {
   /*     DeviceCredentialsUpdateNotificationMsg msg = new DeviceCredentialsUpdateNotificationMsg(userId, deviceId);
        Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(deviceId);
        if (address.isPresent()) {
            rpcService.tell(address.get(), msg);
        } else {
            onMsg(msg);
        }*/
    }

    @Override
    public void onDeviceNameOrTypeUpdate(UserId tenantId, DeviceId deviceId, String deviceName, String deviceType) {
     /*   log.info("[{}] Processing onDeviceNameOrTypeUpdate event, deviceName: {}, deviceType: {}", deviceId, deviceName, deviceType);
        DeviceNameOrTypeUpdateMsg msg = new DeviceNameOrTypeUpdateMsg(userId, deviceId, deviceName, deviceType);
        Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(deviceId);
        if (address.isPresent()) {
            rpcService.tell(address.get(), msg);
        } else {
            onMsg(msg);
        }*/
    }
/*
    @Override
    public void onCredentialsUpdate(  DeviceId deviceId) {
        DeviceCredentialsUpdateNotificationMsg msg = new DeviceCredentialsUpdateNotificationMsg(userId, deviceId);
        Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(deviceId);
        if (address.isPresent()) {
            rpcService.tell(address.get(), msg);
        } else {
            onMsg(msg);
        }
    }

    @Override
    public void onDeviceNameOrTypeUpdate(  DeviceId deviceId, String deviceName, String deviceType) {
        log.info("[{}] Processing onDeviceNameOrTypeUpdate event, deviceName: {}, deviceType: {}", deviceId, deviceName, deviceType);
        DeviceNameOrTypeUpdateMsg msg = new DeviceNameOrTypeUpdateMsg(userId, deviceId, deviceName, deviceType);
        Optional<ServerAddress> address = actorContext.getRoutingService().resolveById(deviceId);
        if (address.isPresent()) {
            rpcService.tell(address.get(), msg);
        } else {
            onMsg(msg);
        }
    }*/

    public void broadcast(ToAllNodesMsg msg) {
        //RPC 广播指令
        rpcService.broadcast(msg);
        //Actor 发�?�消�? ，Server-side 接收msg类型 并做出action
        appActor.tell(msg, ActorRef.noSender());
    }

    private void broadcast(ClusterEventMsg msg) {
        this.appActor.tell(msg, ActorRef.noSender());
        this.sessionManagerActor.tell(msg, ActorRef.noSender());
        this.rpcManagerActor.tell(msg, ActorRef.noSender());
    }
}
