package com.github.rulegin.actors.app;

import akka.actor.*;
import akka.actor.SupervisorStrategy.Directive;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.msg.cluster.ClusterEventMsg;
import com.github.rulegin.actors.msg.cluster.ComponentLifecycleMsg;
import com.github.rulegin.actors.msg.plugin.aware.ToPluginActorMsg;
import com.github.rulegin.actors.msg.plugin.torule.ToRuleActorMsg;
import com.github.rulegin.actors.msg.termination.PluginTerminationMsg;
import com.github.rulegin.actors.rule.RuleManager;
import com.github.rulegin.actors.rule.SystemRuleManager;
import com.github.rulegin.actors.service.ContextBasedCreator;
import com.github.rulegin.actors.service.DefaultActorService;
import com.github.rulegin.actors.user.UserActor;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.dao.model.ModelConstants;
import com.github.rulegin.actors.ContextAwareActor;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class AppActor extends ContextAwareActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    public static final UserId SYSTEM_TENANT = new UserId(ModelConstants.NULL_UUID);
    private final RuleManager ruleManager;
    //private final TenantService tenantService;

    private final Map<UserId, ActorRef> actors;


    private AppActor(ActorSystemContext systemContext) {
        super(systemContext);
        log.info("AppActor(systemContext) ");

        this.ruleManager = new SystemRuleManager(systemContext);
        //this.tenantService = systemContext.getTenantService();
        this.actors = new HashMap<>();
    }
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void preStart() {
        logger.info("preStart AppActor ...");
        try {
            ruleManager.init(this.context());

         /*   PageDataIterable<User> tenantIterator = new PageDataIterable<>(
                    link -> tenantService.findUserAdmins(link),
                    ENTITY_PACK_LIMIT);
            for (User user : tenantIterator) {
                logger.info("[{}] Creating user actors", user.getId());
                getOrCreateUserActor(user.getId());
                logger.info("Tenant actors created.");
            }*/

            logger.info("Main system actors started.");
        } catch (Exception e) {
            logger.error(e, "Unknown failure");
        }
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        logger.info("Received message: {}", msg);
       if (msg instanceof ToRuleActorMsg) {

            onToRuleMsg((ToRuleActorMsg) msg);
        }else if (msg instanceof ToPluginActorMsg) {

            onToPluginMsg((ToPluginActorMsg) msg);
        }
        else if (msg instanceof Terminated) {

            processTermination((Terminated) msg);
        } else if (msg instanceof ClusterEventMsg) {

            broadcast(msg);
        } else if (msg instanceof ComponentLifecycleMsg) {

            // Rule and plugin
            onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
        } else if (msg instanceof PluginTerminationMsg) {

            onPluginTerminated((PluginTerminationMsg) msg);
        } else {
            logger.warning("Unknown message: {}!", msg);
        }
    }

    private void onPluginTerminated(PluginTerminationMsg msg) {
       // pluginManager.remove(msg.getId());
    }

    private void broadcast(Object msg) {
        //pluginManager.broadcast(msg);
        actors.values().forEach(actorRef -> actorRef.tell(msg, ActorRef.noSender()));
    }


    private void onComponentLifecycleMsg(ComponentLifecycleMsg msg) {
        ActorRef target = null;
        //if (SYSTEM_TENANT.equals(msg.getUserId())) {
        if (true) {
            //plugin
            if (msg.getPluginId().isPresent()) {
                //target = pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId().get());
            } else if (msg.getRuleId().isPresent()) {
            //Rule
                Optional<ActorRef> ref = ruleManager.update(this.context(), msg.getRuleId().get(), msg.getEvent());
                if (ref.isPresent()) {
                    target = ref.get();
                } else {
                    logger.info("Failed to find actors for Rule: [{}]", msg.getRuleId());
                    return;
                }
            }
        } else {
            target = getOrCreateUserActor(msg.getUserId());
        }
        if (target != null) {
            //�? pluginActor or ruleActor [ComponentLifecycleMsg]
            target.tell(msg, ActorRef.noSender());
        }
    }
    private ActorRef getOrCreateUserActor(UserId tenantId) {
        ActorRef tenantActor = actors.get(tenantId);
        if (tenantActor == null) {
            tenantActor = context().actorOf(Props.create(new UserActor.ActorCreator(systemContext, tenantId)).withDispatcher(DefaultActorService.CORE_DISPATCHER_NAME), tenantId.toString());
            actors.put(tenantId, tenantActor);
        }
        return tenantActor;
    }

    private void processTermination(Terminated message) {
        ActorRef terminated = message.actor();
        if (terminated instanceof LocalActorRef) {
            logger.info("Removed actors: {}", terminated);
        } else {
            throw new IllegalStateException("Remote actors are not supported!");
        }
    }

    public static class ActorCreator extends ContextBasedCreator<AppActor> {

        private static final long serialVersionUID = 1L;

        public ActorCreator(ActorSystemContext context) {
            super(context);
            log.info("AppActor => ActorCreator()");

        }

        @Override
        public AppActor create() throws Exception {
            log.info("AppActor => ActorCreator().create()");

            return new AppActor(context);
        }
    }

    private final SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("1 minute"), new Function<Throwable, Directive>() {
        @Override
        public Directive apply(Throwable t) {
            logger.error(t, "Unknown failure");
            if (t instanceof RuntimeException) {
                return SupervisorStrategy.restart();
            } else {
                return SupervisorStrategy.stop();
            }
        }
    });

    private void onToRuleMsg(ToRuleActorMsg msg) {
        ActorRef target;
        if (SYSTEM_TENANT.equals(msg.getUserId())) {
            target = ruleManager.getOrCreateRuleActor(this.context(), msg.getRuleId());
        } else {
            target = getOrCreateUserActor(msg.getUserId());
        }
        target.tell(msg, ActorRef.noSender());
    }

    private void onToPluginMsg(ToPluginActorMsg msg) {
        ActorRef target;
        if (SYSTEM_TENANT.equals(msg.getPluginTenantId())) {
            target =null ;// pluginManager.getOrCreatePluginActor(this.context(), msg.getPluginId());
        } else {
            target = getOrCreateUserActor(msg.getPluginTenantId());
        }
        target.tell(msg, ActorRef.noSender());
    }
}
