
package cn.neucloud.server.core.process;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;
import cn.neucloud.server.common.component.ConfigurableComponent;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.actors.rule.*;
import cn.neucloud.server.common.data.component.DefineComponent;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.actors.msg.rule.RuleToPluginMsg;
import cn.neucloud.server.actors.msg.wrapper.RuleToPluginMsgWrapper;
import cn.neucloud.server.core.RuleConstants;
import cn.neucloud.server.core.action.plugins.PluginAction;
import cn.neucloud.server.actors.msg.plugin.torule.PluginToRuleMsg;
import cn.neucloud.server.core.rule.*;
import cn.neucloud.server.common.data.component.ComponentLifecycleState;
import cn.neucloud.server.common.data.id.PluginId;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.rule.RuleMetaData;
import cn.neucloud.server.common.exception.RuleInitializationException;
import cn.neucloud.server.actors.msg.cluster.ClusterEventMsg;
import cn.neucloud.server.actors.msg.RuleEngineError;
import cn.neucloud.server.actors.msg.termination.RuleTerminationMsg;
import cn.neucloud.server.actors.msg.core.RuleToPluginTimeoutMsg;
import cn.neucloud.server.actors.ActorSystemContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class RuleActorMessageProcessor extends ComponentMsgProcessor<RuleId> {

    private final RuleProcessingContext ruleCtx;
    private final Map<UUID, RuleProcessingMsg> pendingMsgMap;

    private RuleMetaData ruleMd;
    private DefineComponent defineComponent;
    private ComponentLifecycleState state;

    private RuleDataSource dataSource;
    private List<RuleFilter> filters;
    private RuleProcessor processor;
    private PluginAction action;

    private UserId userId;
    private PluginId pluginId;

    public RuleActorMessageProcessor(RuleId ruleId, UserId userId, ActorSystemContext systemContext, LoggingAdapter logger) {
        super(systemContext, logger, userId, ruleId);
        this.pendingMsgMap = new HashMap<>();
        this.ruleCtx = new RuleProcessingContext(systemContext, ruleId);
    }

    @Override
    public void start() throws Exception {
        logger.info("[{}] Going to start actors of Rule", entityId);
        ruleMd = systemContext.getRuleService().findRuleById(entityId);
        if (ruleMd == null) {
            throw new RuleInitializationException("Rule not found!");
        }
        state = ruleMd.getState();
        if (state == ComponentLifecycleState.ACTIVE) {
            logger.info("[{}] Rule is active. Going to initialize Rule components.", entityId);
            //
            initComponent();
        } else {
            logger.info("[{}] Rule is suspended. Skipping Rule components initialization.", entityId);
        }

        logger.info("[{}] Started Rule actors.", entityId);
    }

    @Override
    public void stop() throws Exception {
        onStop();
    }


    private void initComponent() throws RuleException {
        try {
            if (!ruleMd.getFilters().isArray()) {
                throw new RuntimeException("Filters are not array!");
            }
            //fetchPluginInfo();
            //initDataSource();
            //initFilters();
            //initProcessor();
            //initActions();
            // TODO: 2018/1/19 0019
            //activeRule(ruleMd);


            JstartComponent();
        } catch (RuntimeException e) {
            throw new RuleInitializationException("Unknown runtime exception!", e);
        } catch (InstantiationException e) {
            throw new RuleInitializationException("No default constructor for Rule implementation!", e);
        } catch (IllegalAccessException e) {
            throw new RuleInitializationException("Illegal Access Exception during Rule initialization!", e);
        } catch (ClassNotFoundException e) {
            throw new RuleInitializationException("Rule Class not found!", e);
        } catch (Exception e) {
            throw new RuleException(e.getMessage(), e);
        }
    }

    // TODO: 2018/1/19 0019
    protected <T extends ConfigurableComponent> T JstartComponent() throws Exception {

        JsonNode dataSourceNode = ruleMd.getDataSource();


        // TODO: 2018/1/19 0019 1
        /*
        将所有信息汇集 注解到配置文件中
    {
      "topic": "aliyun-iot-YWYfetAm9Wh",
      "format": "json",
      "keys": [
        "Suct_Pres_Status",
        "asdfasdf"
      ],
      "condition": "Suct_Pres_Status > 70"
    }
    */
        String dataType = dataSourceNode.get(RuleConstants.TYPE).textValue();
        ObjectNode dataConfi = (ObjectNode) dataSourceNode.get(RuleConstants.CONFIGURATION);

        JsonNode filtersNodeArray = ruleMd.getFilters();
        String filterType = null;
        String condition = null;
        ObjectNode filterConfig = null;
        for (int i = 0; i < filtersNodeArray.size(); i++) {
            ObjectNode filternode = (ObjectNode) filtersNodeArray.get(i);
            filterType = filternode.get(RuleConstants.TYPE).textValue();
            condition = filternode.get(RuleConstants.CONDITION).textValue();
            filterConfig = (ObjectNode) filternode.get(RuleConstants.CONFIGURATION);
        }

        dataConfi.put(RuleConstants.CONDITION,condition);

        String clazz = null;
        if(RuleConstants.FILTERS_TYPE_SINGLE.equals(filterType)){

            this.defineComponent = systemContext.getDefineComponentService().findByDataSourceTypeAndFiltersType(dataType, filterType);
            clazz = defineComponent.getClazz();

        }else if (RuleConstants.FILTERS_TYPE_KAFKAWINDOW.equals(filterType)){
            dataConfi.put(RuleConstants.WINDOW_SIZE,filterConfig.get(RuleConstants.WINDOW_SIZE).asText());
            dataConfi.put(RuleConstants.WINDOW_STEP,filterConfig.get(RuleConstants.WINDOW_STEP).asText());
            this.defineComponent = systemContext.getDefineComponentService().findByDataSourceTypeAndFiltersType(dataType, filterType);
            clazz = defineComponent.getClazz();

        }
        logger.info("deal with JsonNode: {}", filterType);

        return startComponent(dataConfi, clazz);

    }

    private void initDataSource() {
        logger.info("run datasouce ing ");
        if (ruleMd.getDataSource() != null && !ruleMd.getDataSource().isNull()) {
            try {
                dataSource = JinitComponent(ruleMd.getDataSource(), ComponentType.RULE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initFilters() throws Exception {
        logger.info("initing filter ");
        filters = new ArrayList<>(ruleMd.getFilters().size());
        for (int i = 0; i < ruleMd.getFilters().size(); i++) {
            //filters.add(initComponent(ruleMd.getFilters().get(i)));
            filters.add(JinitComponent(ruleMd.getFilters().get(i), ComponentType.FILTER));
        }
    }

    private void initActions() throws Exception {
        logger.info("run actionsType ing ");
        if (ruleMd.getActions() != null && !ruleMd.getActions().isNull()) {
            //actionsType = initComponent(ruleMd.getActionsType());
            action = JinitComponent(ruleMd.getActions(), ComponentType.ACTION);
        }
    }

    public void onPluginMsg(ActorContext context, PluginToRuleMsg<?> msg) {
        RuleProcessingMsg pendingMsg = pendingMsgMap.remove(msg.getUid());
        if (pendingMsg != null) {
            ChainProcessingContext ctx = pendingMsg.getCtx();
           /* Optional<ToDeviceMsg> ruleResponseOptional = actionsType.convert(msg);
            if (ruleResponseOptional.isPresent()) {
                ctx.mergeResponse(ruleResponseOptional.get());
                pushToNextRule(context, ctx, null);
            } else {
                pushToNextRule(context, ctx, RuleEngineError.NO_RESPONSE_FROM_ACTIONS);
            }*/
            pushToNextRule(context, ctx, RuleEngineError.NO_RESPONSE_FROM_ACTIONS);

        } else {
            logger.warning("[{}] Processing timeout detected: [{}]", entityId, msg.getUid());
        }
    }

    public void onTimeoutMsg(ActorContext context, RuleToPluginTimeoutMsg msg) {
        RuleProcessingMsg pendingMsg = pendingMsgMap.remove(msg.getMsgId());
        if (pendingMsg != null) {
            logger.debug("[{}] Processing timeout detected [{}]: {}", entityId, msg.getMsgId(), pendingMsg);
            ChainProcessingContext ctx = pendingMsg.getCtx();
            pushToNextRule(context, ctx, RuleEngineError.PLUGIN_TIMEOUT);
        }
    }

    private void pushToNextRule(ActorContext context, ChainProcessingContext ctx, RuleEngineError error) {
        if (error != null) {
            ctx = ctx.withError(error);
        }
        if (ctx.isFailure()) {
            logger.info("[{}][{}] Forwarding processing chain to device actors due to failure.", ruleMd.getId());
            ctx.getDeviceActor().tell(new RulesProcessedMsg(ctx), ActorRef.noSender());
        } else if (!ctx.hasNext()) {
            logger.info("[{}][{}] Forwarding processing chain to device actors due to end of chain.", ruleMd.getId());
            ctx.getDeviceActor().tell(new RulesProcessedMsg(ctx), ActorRef.noSender());
        } else {
            logger.info("[{}][{}] Forwarding processing chain to next Rule actors.", ruleMd.getId());
            ChainProcessingContext nextTask = ctx.getNext();
            nextTask.getCurrentActor().tell(new RuleProcessingMsg(nextTask), context.self());
        }
    }

    @Override
    public void onCreated(ActorContext context) {
        logger.info("[{}] Going to process onCreated Rule.", entityId);
    }

    @Override
    public void onUpdate(ActorContext context) throws RuleException {
        RuleMetaData oldRuleMd = ruleMd;
        ruleMd = systemContext.getRuleService().findRuleById(entityId);
        logger.info("[{}] Rule configuration was updated from {} to {}.", entityId, oldRuleMd, ruleMd);
        try {
            //fetchPluginInfo();
            if (filters == null || !Objects.equals(oldRuleMd.getFilters(), ruleMd.getFilters())) {
                logger.info("[{}] Rule filters require restart due to json change from {} to {}.",
                        entityId, mapper.writeValueAsString(oldRuleMd.getFilters()), mapper.writeValueAsString(ruleMd.getFilters()));
                stopFilters();
                initFilters();
            }
          /*  if (processor == null || !Objects.equals(oldRuleMd.getProcessor(), ruleMd.getProcessor())) {
                logger.info("[{}] Rule processor require restart due to configuration change.", entityId);
                stopProcessor();
                //initProcessor();
            }*/
            if (action == null || !Objects.equals(oldRuleMd.getActions(), ruleMd.getActions())) {
                logger.info("[{}] Rule actionsType require restart due to configuration change.", entityId);
                stopAction();
                initActions();
            }
        } catch (RuntimeException e) {
            throw new RuleInitializationException("Unknown runtime exception!", e);
        } catch (InstantiationException e) {
            throw new RuleInitializationException("No default constructor for Rule implementation!", e);
        } catch (IllegalAccessException e) {
            throw new RuleInitializationException("Illegal Access Exception during Rule initialization!", e);
        } catch (ClassNotFoundException e) {
            throw new RuleInitializationException("Rule Class not found!", e);
        } catch (JsonProcessingException e) {
            throw new RuleInitializationException("Rule configuration is invalid!", e);
        } catch (Exception e) {
            throw new RuleInitializationException(e.getMessage(), e);
        }
    }

    @Override
    public void onActivate(ActorContext context) throws Exception {
        logger.info("[{}] Going to process onActivate Rule.", entityId);
        this.state = ComponentLifecycleState.ACTIVE;
        if (filters != null) {
            filters.forEach(RuleLifecycleComponent::resume);
            if (processor != null) {
                processor.resume();
            } else {
                //initProcessor();
            }
            if (action != null) {
                action.resume();
            }
            logger.info("[{}] Rule resumed.", entityId);
        } else {
            logger.info(" or start");
            start();
        }
    }

    @Override
    public void onSuspend(ActorContext context) {
        logger.info("[{}] Going to process onSuspend Rule.", entityId);
        this.state = ComponentLifecycleState.SUSPENDED;
        if (filters != null) {
            filters.forEach(f -> f.suspend());
        }
        if (processor != null) {
            processor.suspend();
        }
        if (action != null) {
            action.suspend();
        }
    }

    @Override
    public void onStop(ActorContext context) {
        logger.info("[{}] Going to process onStop Rule.", entityId);
        onStop();
        scheduleMsgWithDelay(context, new RuleTerminationMsg(entityId), systemContext.getRuleActorTerminationDelay());
    }

    @Override
    public void onClusterEventMsg(ClusterEventMsg msg) throws Exception {

    }

    // here is  to process Rule logic
    public void onRuleProcessingMsg(ActorContext context, RuleProcessingMsg msg) throws RuleException {
        logger.info("pushToNextRule: {}", state);
        if (state != ComponentLifecycleState.ACTIVE) {
            logger.info("pushToNextRule: state={} , NO_ACTIVE_RULES", state);
            pushToNextRule(context, msg.getCtx(), RuleEngineError.NO_ACTIVE_RULES);
            return;
        }
        ChainProcessingContext chainCtx = msg.getCtx();

        logger.info("[{}] Going to filter in msg: {}", entityId);
        for (RuleFilter filter : filters) {
            if (!filter.filter(ruleCtx)) {
                logger.info("[{}] In msg is NOT valid for processing by current Rule: {}", entityId);
                pushToNextRule(context, msg.getCtx(), RuleEngineError.NO_FILTERS_MATCHED);
                return;
            }
        }
        RuleProcessingMetaData inMsgMd;
        if (processor != null) {
            logger.info("[{}] Going to process in msg: {}", entityId);
            inMsgMd = processor.process(ruleCtx);
        } else {
            inMsgMd = new RuleProcessingMetaData();
        }
        logger.info("[{}] Going to convert in msg: {}", entityId);
        if (action != null) {
            Optional<RuleToPluginMsg<?>> ruleToPluginMsgOptional = action.convert(ruleCtx, inMsgMd);
            if (ruleToPluginMsgOptional.isPresent()) {
                RuleToPluginMsg<?> ruleToPluginMsg = ruleToPluginMsgOptional.get();
                logger.info("[{}] Device msg is converter to: {}", entityId, ruleToPluginMsg);
                context.parent().tell(new RuleToPluginMsgWrapper(pluginId, entityId, ruleToPluginMsg, userId), context.self());
                if (action.isOneWayAction()) {
                    pushToNextRule(context, msg.getCtx(), RuleEngineError.NO_TWO_WAY_ACTIONS);
                    return;
                } else {
                    pendingMsgMap.put(ruleToPluginMsg.getUid(), msg);
                    scheduleMsgWithDelay(context, new RuleToPluginTimeoutMsg(ruleToPluginMsg.getUid()), systemContext.getPluginProcessingTimeout());
                    return;
                }
            }
        }
        logger.info("[{}] Nothing to send to plugin: {}", entityId, pluginId);
        pushToNextRule(context, msg.getCtx(), RuleEngineError.NO_TWO_WAY_ACTIONS);
    }

    private void onStop() {
        this.state = ComponentLifecycleState.SUSPENDED;
        stopFilters();
        stopAction();
    }


    private void stopAction() {
        if (action != null) {
            action.stop();
        }
    }

    private void stopFilters() {
        if (filters != null) {
            filters.forEach(f -> f.stop());
        }
    }
}
