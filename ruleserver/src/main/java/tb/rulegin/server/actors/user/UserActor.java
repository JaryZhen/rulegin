package tb.rulegin.server.actors.user;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.msg.termination.PluginTerminationMsg;
import tb.rulegin.server.actors.rule.RuleManager;
import tb.rulegin.server.actors.rule.UserRuleManager;
import tb.rulegin.server.actors.ContextAwareActor;
import tb.rulegin.server.actors.service.ContextBasedCreator;
import tb.rulegin.server.common.data.id.DeviceId;
import tb.rulegin.server.actors.msg.cluster.ComponentLifecycleMsg;
import tb.rulegin.server.actors.msg.cluster.ClusterEventMsg;
import tb.rulegin.server.actors.msg.plugin.aware.ToPluginActorMsg;
import tb.rulegin.server.actors.msg.plugin.torule.ToRuleActorMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserActor extends ContextAwareActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final UserId tenantId;
    private final RuleManager ruleManager;
    //private final PluginManager pluginManager;
    private final Map<DeviceId, ActorRef> deviceActors;

    private UserActor(ActorSystemContext systemContext, UserId tenantId) {
        super(systemContext);
        this.tenantId = tenantId;
        this.ruleManager = new UserRuleManager(systemContext, tenantId);
        //this.pluginManager = new UserPluginManager(systemContext, tenantId);
        this.deviceActors = new HashMap<>();
    }

    @Override
    public void preStart() {
        logger.info("[{}] Starting user actors.", tenantId);
        try {
            ruleManager.init(this.context());
            //pluginManager.run(this.context());
            logger.info("[{}] Tenant actors started.", tenantId);
        } catch (Exception e) {
            logger.error(e, "[{}] Unknown failure", tenantId);
        }
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        logger.debug("[{}] Received message: {}", tenantId, msg);
       if (msg instanceof ToPluginActorMsg) {

            onToPluginMsg((ToPluginActorMsg) msg);
        } else if (msg instanceof ToRuleActorMsg) {

            onToRuleMsg((ToRuleActorMsg) msg);
        } else if (msg instanceof ClusterEventMsg) {

            broadcast(msg);
        } else if (msg instanceof ComponentLifecycleMsg) {

            onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
        } else if (msg instanceof PluginTerminationMsg) {

            onPluginTerminated((PluginTerminationMsg) msg);
        } else {
            logger.warning("[{}] Unknown message: {}!", tenantId, msg);
        }
    }

    private void broadcast(Object msg) {
        //pluginManager.broadcast(msg);
        deviceActors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
    }

    private void onToRuleMsg(ToRuleActorMsg msg) {
        ActorRef target = ruleManager.getOrCreateRuleActor(this.context(), msg.getRuleId());
        target.tell(msg, ActorRef.noSender());
    }

    private void onToPluginMsg(ToPluginActorMsg msg) {
        if (msg.getPluginTenantId().equals(tenantId)) {
            ActorRef pluginActor = null ;//pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId());
            pluginActor.tell(msg, ActorRef.noSender());
        } else {
            context().parent().tell(msg, ActorRef.noSender());
        }
    }

    private void onComponentLifecycleMsg(ComponentLifecycleMsg msg) {
        if (msg.getPluginId().isPresent()) {
            ActorRef pluginActor = null ;//pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId().get());
            pluginActor.tell(msg, ActorRef.noSender());
        } else if (msg.getRuleId().isPresent()) {
            ActorRef target;
            Optional<ActorRef> ref = ruleManager.update(this.context(), msg.getRuleId().get(), msg.getEvent());
            if (ref.isPresent()) {
                target = ref.get();
            } else {
                logger.debug("Failed to find actors for Rule: [{}]", msg.getRuleId());
                return;
            }
            target.tell(msg, ActorRef.noSender());
        } else {
            logger.debug("[{}] Invalid component lifecycle msg.", tenantId);
        }
    }

    private void onPluginTerminated(PluginTerminationMsg msg) {
        //pluginManager.remove(msg.getId());
    }


    private ActorRef getOrCreateDeviceActor(DeviceId deviceId) {
        ActorRef deviceActor = deviceActors.get(deviceId);
        if (deviceActor == null) {
            //deviceActor = context().actorOf(Props.create(new DeviceActor.ActorCreator(systemContext, userId, deviceId))
               //     .withDispatcher(DefaultActorService.CORE_DISPATCHER_NAME), deviceId.toString());
            deviceActors.put(deviceId, deviceActor);
        }
        return deviceActor;
    }

    public static class ActorCreator extends ContextBasedCreator<UserActor> {
        private static final long serialVersionUID = 1L;

        private final UserId tenantId;

        public ActorCreator(ActorSystemContext context, UserId tenantId) {
            super(context);
            this.tenantId = tenantId;
        }

        @Override
        public UserActor create() throws Exception {
            return new UserActor(context, tenantId);
        }
    }

}
