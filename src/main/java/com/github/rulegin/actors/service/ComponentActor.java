package com.github.rulegin.actors.service;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.github.rulegin.actors.msg.cluster.ClusterEventMsg;
import com.github.rulegin.actors.msg.cluster.ComponentLifecycleMsg;
import com.github.rulegin.actors.stats.StatsPersistMsg;
import com.github.rulegin.actors.ActorSystemContext;
import com.github.rulegin.actors.ContextAwareActor;
import com.github.rulegin.common.data.component.ComponentLifecycleEvent;
import com.github.rulegin.common.data.id.EntityId;
import com.github.rulegin.core.process.ComponentMsgProcessor;

import static com.github.rulegin.common.data.component.ComponentLifecycleEvent.DELETED;

public abstract class ComponentActor<T extends EntityId, P extends ComponentMsgProcessor<T>> extends ContextAwareActor {

    protected final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private long lastPersistedErrorTs = 0L;
    protected final T id;
    protected P processor;
    private long messagesProcessed;
    private long errorsOccurred;

    public ComponentActor(ActorSystemContext systemContext, T id) {
        super(systemContext);
        this.id = id;
    }

    protected void setProcessor(P processor) {
        this.processor = processor;
    }

    @Override
    public void preStart() {
        try {
            //RuleActorMessageProcessor
            logger.info("which processor : {}",processor.getClass().getName());
            processor.start();
            logLifecycleEvent(ComponentLifecycleEvent.STARTED);
            if (systemContext.isStatisticsEnabled()) {
                scheduleStatsPersistTick();
            }
        } catch (Exception e) {
            logger.warning("[{}][{}] Failed to start {} processor: {}",   id, id.getEntityType(), e);
            logAndPersist("OnStart", e, true);
            logLifecycleEvent(ComponentLifecycleEvent.STARTED, e);
        }
    }

    private void scheduleStatsPersistTick() {
        try {
            processor.scheduleStatsPersistTick(context(), systemContext.getStatisticsPersistFrequency());
        } catch (Exception e) {
            logger.error("[{}][{}] Failed to schedule statistics store message. No statistics is going to be stored: {}", id, e.getMessage());
            logAndPersist("onScheduleStatsPersistMsg", e);
        }
    }

    @Override
    public void postStop() {
        try {
            processor.stop();
            logLifecycleEvent(ComponentLifecycleEvent.STOPPED);
        } catch (Exception e) {
            logger.warning("[{}][{}] Failed to stop {} processor: {}",   id, id.getEntityType(), e.getMessage());
            logAndPersist("OnStop", e, true);
            logLifecycleEvent(ComponentLifecycleEvent.STOPPED, e);
        }
    }

    protected void onComponentLifecycleMsg(ComponentLifecycleMsg msg) {
        try {
            switch (msg.getEvent()) {
                case CREATED:
                    processor.onCreated(context());
                    break;
                case UPDATED:
                    processor.onUpdate(context());
                    break;
                case ACTIVATED:
                    processor.onActivate(context());
                    break;
                case SUSPENDED:
                    processor.onSuspend(context());
                    break;
                case DELETED:
                    processor.onStop(context());
            }
            logLifecycleEvent(msg.getEvent());
        } catch (Exception e) {
            logAndPersist("onLifecycleMsg", e, true);
            logLifecycleEvent(msg.getEvent(), e);
        }
    }

    protected void onClusterEventMsg(ClusterEventMsg msg) {
        try {
            processor.onClusterEventMsg(msg);
        } catch (Exception e) {
            logAndPersist("onClusterEventMsg", e);
        }
    }

    protected void onStatsPersistTick(EntityId entityId) {
        try {
            systemContext.getStatsActor().tell(new StatsPersistMsg(messagesProcessed, errorsOccurred,   entityId), ActorRef.noSender());
            resetStatsCounters();
        } catch (Exception e) {
            logAndPersist("onStatsPersistTick", e);
        }
    }

    private void resetStatsCounters() {
        messagesProcessed = 0;
        errorsOccurred = 0;
    }

    protected void increaseMessagesProcessedCount() {
        messagesProcessed++;
    }


    protected void logAndPersist(String method, Exception e) {
        logAndPersist(method, e, false);
    }

    private void logAndPersist(String method, Exception e, boolean critical) {
        errorsOccurred++;
        if (critical) {
            logger.warning("[{}][{}] Failed to process {} msg: {}", id,   method, e);
        } else {
            logger.info("[{}][{}] Failed to process {} msg: {}", id,   method, e);
        }
        long ts = System.currentTimeMillis();
        if (ts - lastPersistedErrorTs > getErrorPersistFrequency()) {
            systemContext.persistError(  id, method, e);
            lastPersistedErrorTs = ts;
        }
    }

    protected void logLifecycleEvent(ComponentLifecycleEvent event) {
        logLifecycleEvent(event, null);
    }

    protected void logLifecycleEvent(ComponentLifecycleEvent event, Exception e) {
        systemContext.persistLifecycleEvent( id, event, e);
    }

    protected abstract long getErrorPersistFrequency();
}
