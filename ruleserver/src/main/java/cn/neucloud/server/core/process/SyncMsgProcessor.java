package cn.neucloud.server.core.process;

import akka.actor.ActorContext;
import akka.event.LoggingAdapter;
import cn.neucloud.server.actors.ActorSystemContext;
import cn.neucloud.server.common.data.id.SessionId;
import cn.neucloud.server.actors.msg.cluster.ClusterEventMsg;
import cn.neucloud.server.actors.service.cluster.ServerAddress;
import cn.neucloud.server.actors.msg.session.*;
import cn.neucloud.server.common.exception.msg.SessionException;
import cn.neucloud.server.actors.msg.core.SessionTimeoutMsg;

import java.util.Optional;

public class SyncMsgProcessor extends AbstractSessionActorMsgProcessor {
    private Optional<ServerAddress> currentTargetServer;
    private boolean pendingResponse;

    public SyncMsgProcessor(ActorSystemContext ctx, LoggingAdapter logger, SessionId sessionId) {
        super(ctx, logger, sessionId);
    }

    public void processTimeoutMsg(ActorContext context, SessionTimeoutMsg msg) {
        if (pendingResponse) {
            try {
                sessionCtx.onMsg(SessionCloseMsg.onTimeout(sessionId));
            } catch (SessionException e) {
                logger.warning("Failed to push session close msg", e);
            }
            terminateSession(context, this.sessionId);
        }
    }


    @Override
    public void processClusterEvent(ActorContext context, ClusterEventMsg msg) {
        if (pendingResponse) {
            Optional<ServerAddress> newTargetServer = null;//forwardToAppActorIfAdressChanged(context, pendingMsg, currentTargetServer);
            if (logger.isDebugEnabled()) {
                if (!newTargetServer.equals(currentTargetServer)) {
                    if (newTargetServer.isPresent()) {
                        logger.debug("[{}] Forwarded msg to new server: {}", sessionId, newTargetServer.get());
                    } else {
                        logger.debug("[{}] Forwarded msg to local server.", sessionId);
                    }
                }
            }
            currentTargetServer = newTargetServer;
        }
    }

    private long getTimeout(ActorSystemContext ctx, SessionContext sessionCtx) {
        return sessionCtx.getTimeout() > 0 ? sessionCtx.getTimeout() : ctx.getSyncSessionTimeout();
    }
}
