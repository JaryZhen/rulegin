
package cn.neucloud.server.core.process;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;
import cn.neucloud.server.actors.ActorSystemContext;
import cn.neucloud.server.common.data.id.SessionId;
import cn.neucloud.server.actors.msg.cluster.ClusterEventMsg;
import cn.neucloud.server.actors.msg.session.*;
import cn.neucloud.server.actors.msg.core.SessionTimeoutMsg;
import cn.neucloud.server.actors.msg.termination.SessionTerminationMsg;

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
