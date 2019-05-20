
package com.github.rulegin.core.process;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;
import com.github.rulegin.actors.msg.cluster.ClusterEventMsg;
import com.github.rulegin.actors.msg.core.SessionTimeoutMsg;
import com.github.rulegin.actors.msg.session.SessionCloseMsg;
import com.github.rulegin.actors.msg.session.SessionContext;
import com.github.rulegin.actors.msg.session.SessionCtrlMsg;
import com.github.rulegin.actors.msg.termination.SessionTerminationMsg;
import com.github.rulegin.common.data.id.SessionId;
import com.github.rulegin.actors.ActorSystemContext;

public abstract class AbstractSessionActorMsgProcessor extends AbstractContextAwareMsgProcessor {

    protected final SessionId sessionId;
    protected SessionContext sessionCtx;

    protected AbstractSessionActorMsgProcessor(ActorSystemContext ctx, LoggingAdapter logger, SessionId sessionId) {
        super(ctx, logger);
        this.sessionId = sessionId;
    }


    public abstract void processTimeoutMsg(ActorContext context, SessionTimeoutMsg msg);


    public abstract void processClusterEvent(ActorContext context, ClusterEventMsg msg);

    public void processSessionCtrlMsg(ActorContext ctx, SessionCtrlMsg msg) {
        if (msg instanceof SessionCloseMsg) {
            cleanupSession(ctx);
            terminateSession(ctx, sessionId);
        }
    }

    protected void cleanupSession(ActorContext ctx) {
    }

    public static void terminateSession(ActorContext ctx, SessionId sessionId) {
        ctx.parent().tell(new SessionTerminationMsg(sessionId), ActorRef.noSender());
        ctx.stop(ctx.self());
    }
}
