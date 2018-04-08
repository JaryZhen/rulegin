package cn.neucloud.server.actors.session;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cn.neucloud.server.actors.ActorSystemContext;
import cn.neucloud.server.actors.ContextAwareActor;
import cn.neucloud.server.actors.service.ContextBasedCreator;
import cn.neucloud.server.common.data.id.SessionId;
import cn.neucloud.server.actors.msg.cluster.ClusterEventMsg;
import cn.neucloud.server.actors.msg.session.*;
import cn.neucloud.server.actors.msg.core.SessionTimeoutMsg;
import cn.neucloud.server.core.process.AbstractSessionActorMsgProcessor;
import scala.concurrent.duration.Duration;

public class SessionActor extends ContextAwareActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final SessionId sessionId;
    private AbstractSessionActorMsgProcessor processor;

    private SessionActor(ActorSystemContext systemContext, SessionId sessionId) {
        super(systemContext);
        this.sessionId = sessionId;
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(-1, Duration.Inf(),
                throwable -> {
                    logger.error(throwable, "Unknown session error");
                    if (throwable instanceof Error) {
                        return OneForOneStrategy.escalate();
                    } else {
                        return OneForOneStrategy.resume();
                    }
                });
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        logger.debug("[{}] Processing: {}.", sessionId, msg);
        if (msg instanceof SessionTimeoutMsg) {
            processTimeoutMsg((SessionTimeoutMsg) msg);
        } else if (msg instanceof SessionCtrlMsg) {
            processSessionCtrlMsg((SessionCtrlMsg) msg);
        } else if (msg instanceof ClusterEventMsg) {
            processClusterEvent((ClusterEventMsg) msg);
        } else {
            logger.warning("[{}] Unknown msg: {}", sessionId, msg);
        }
    }

    private void processClusterEvent(ClusterEventMsg msg) {
        processor.processClusterEvent(context(), msg);
    }


    private void processTimeoutMsg(SessionTimeoutMsg msg) {
        if (processor != null) {
            processor.processTimeoutMsg(context(), msg);
        } else {
            logger.warning("[{}] Can't process timeout msg: {} without processor", sessionId, msg);
        }
    }

    private void processSessionCtrlMsg(SessionCtrlMsg msg) {
        if (processor != null) {
            processor.processSessionCtrlMsg(context(), msg);
        } else if (msg instanceof SessionCloseMsg) {
            AbstractSessionActorMsgProcessor.terminateSession(context(), sessionId);
        } else {
            logger.warning("[{}] Can't process session ctrl msg: {} without processor", sessionId, msg);
        }
    }

    public static class ActorCreator extends ContextBasedCreator<SessionActor> {
        private static final long serialVersionUID = 1L;

        private final SessionId sessionId;

        public ActorCreator(ActorSystemContext context, SessionId sessionId) {
            super(context);
            this.sessionId = sessionId;
        }

        @Override
        public SessionActor create() throws Exception {
            return new SessionActor(context, sessionId);
        }
    }

}
