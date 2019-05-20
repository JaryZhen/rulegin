
package com.github.rulegin.core.process;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Scheduler;
import akka.event.LoggingAdapter;
import com.github.rulegin.common.component.*;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.actors.ActorSystemContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractContextAwareMsgProcessor {

    protected final ActorSystemContext systemContext;
    protected final LoggingAdapter logger;
    protected final ObjectMapper mapper = new ObjectMapper();

    protected AbstractContextAwareMsgProcessor(ActorSystemContext systemContext, LoggingAdapter logger) {
        super();
        this.systemContext = systemContext;
        this.logger = logger;
    }

    protected ActorRef getAppActor() {
        return systemContext.getAppActor();
    }

    protected Scheduler getScheduler() {
        return systemContext.getScheduler();
    }

    protected ExecutionContextExecutor getSystemDispatcher() {
        return systemContext.getActorSystem().dispatcher();
    }

    protected void schedulePeriodicMsgWithDelay(ActorContext ctx, Object msg, long delayInMs, long periodInMs) {
        schedulePeriodicMsgWithDelay(ctx, msg, delayInMs, periodInMs, ctx.self());
    }

    protected void schedulePeriodicMsgWithDelay(ActorContext ctx, Object msg, long delayInMs, long periodInMs, ActorRef target) {
        logger.info("Scheduling periodic msg {} every {} ms with delay {} ms", msg, periodInMs, delayInMs);
        getScheduler().schedule(Duration.create(delayInMs, TimeUnit.MILLISECONDS), Duration.create(periodInMs, TimeUnit.MILLISECONDS), target, msg, getSystemDispatcher(), null);
    }


    protected void scheduleMsgWithDelay(ActorContext ctx, Object msg, long delayInMs) {
        scheduleMsgWithDelay(ctx, msg, delayInMs, ctx.self());
    }

    protected void scheduleMsgWithDelay(ActorContext ctx, Object msg, long delayInMs, ActorRef target) {
        logger.info("Scheduling msg {} with delay {} ms", msg, delayInMs);
        getScheduler().scheduleOnce(Duration.create(delayInMs, TimeUnit.MILLISECONDS), target, msg, getSystemDispatcher(), null);
    }

    //deal with componentNode's actionsType or filter
    protected <T extends ConfigurableComponent> T JinitComponent(JsonNode componentNode, ComponentType type) throws Exception {
        logger.info("deal with JsonNode: {}", componentNode.toString());
        ComponentConfiguration configuration = new ComponentConfiguration(
                componentNode.get("clazz").asText(),
                componentNode.get("name").asText(),
                mapper.writeValueAsString(componentNode.get("configuration"))
        );
        logger.info("Initializing [{}][{}] component", configuration.getName(), configuration.getClazz());
        logger.info("what's up 0  ");

        return initComponent(configuration.getClazz(), type, configuration.getConfiguration());
    }


/*

    protected <T extends ConfigurableComponent> T initComponent(JsonNode componentNode) throws Exception {
        logger.info("deal with JsonNode: {}", componentNode.toString());
        ComponentConfiguration configuration = new ComponentConfiguration(
                componentNode.get("clazz").asText(),
                componentNode.get("name").asText(),
                mapper.writeValueAsString(componentNode.get("configuration"))
        );

        return initComponent(componentDescriptor, configuration);
    }

    protected <T extends ConfigurableComponent> T initComponent(ComponentDescriptor componentDefinition, ComponentConfiguration configuration)
            throws Exception {
        return initComponent(componentDefinition.getClazz(), componentDefinition.getType(), configuration.getConfiguration());
    }
*/


    protected <T extends ConfigurableComponent> T startComponent( JsonNode configuration,String clazz)
            throws Exception {

        Class<?> componentClazz = Class.forName(clazz);
        T component = (T) (componentClazz.newInstance());
        Class<?> configurationClazz;
        configurationClazz = ((Rule) componentClazz.getAnnotation(Rule.class)).configuration();

        //这里执行filter的init方法 ，然后init执行filter方法 configuration == "filter": "typeof temperature !== 'undefined' && temperature >= 100"
        logger.info("{}.run(decode({},{})) ", component.getClass().getSimpleName(), configuration, configurationClazz);
        // Rule:KafkaPluginAction  MsgTypeFilter; plugin: KafkaPlugin
        component.run(decode(configuration.toString(), configurationClazz));
        return component;
    }

    //deal with componentNode's actionsType or filter
    protected <T extends ConfigurableComponent> T initComponent(String clazz, ComponentType type, String configuration)
            throws Exception {
        logger.info("swich type {}", type);
        Class<?> componentClazz = Class.forName(clazz);
        T component = (T) (componentClazz.newInstance());
        Class<?> configurationClazz;
        switch (type) {
            //匹配filter
            case FILTER:
                //DeviceFilterConfiguration
                configurationClazz = ((Filter) componentClazz.getAnnotation(Filter.class)).configuration();
                break;
            case PROCESSOR:
                configurationClazz = ((Processor) componentClazz.getAnnotation(Processor.class)).configuration();
                break;
            case ACTION:
                configurationClazz = ((Action) componentClazz.getAnnotation(Action.class)).configuration();
                break;
            case PLUGIN:
                //  kafka plugin so on ...
                configurationClazz = ((Plugin) componentClazz.getAnnotation(Plugin.class)).configuration();
                break;
            default:
                throw new IllegalStateException("Component with type: " + type + " is not supported!");
        }

        logger.info("{}.run(decode({},{})) ", component.getClass().getName(), configuration, configurationClazz);
        // Rule:KafkaPluginAction  MsgTypeFilter; plugin: KafkaPlugin
        component.run(decode(configuration, configurationClazz));
        return component;
    }




    public <C> C decode(String configuration, Class<C> configurationClazz) throws IOException, RuntimeException {
        logger.info("Initializing using configuration: {}", configuration);
        return mapper.readValue(configuration, configurationClazz);
    }

    @Data
    @AllArgsConstructor
    private static class ComponentConfiguration {
        private final String clazz;
        private final String name;
        private final String configuration;
    }

}
