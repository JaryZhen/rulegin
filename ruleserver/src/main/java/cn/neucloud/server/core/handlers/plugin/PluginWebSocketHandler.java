
package cn.neucloud.server.core.handlers.plugin;

import cn.neucloud.server.actors.msg.plugin.aware.PluginWebsocketMsg;
import cn.neucloud.server.actors.msg.ws.PluginWebsocketSessionRef;
import cn.neucloud.server.actors.msg.core.SessionEvent;
import cn.neucloud.server.actors.msg.plugin.aware.SessionEventPluginWebSocketMsg;
import cn.neucloud.server.actors.msg.plugin.aware.TextPluginWebSocketMsg;
import cn.neucloud.server.actors.service.ActorService;
import cn.neucloud.server.dao.plugin.PluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class PluginWebSocketHandler extends TextWebSocketHandler implements PluginWebSocketMsgEndpoint {

    private static final ConcurrentMap<String, SessionMetaData> internalSessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> externalSessionMap = new ConcurrentHashMap<>();

    @Autowired
    @Lazy
    private ActorService actorService;

    @Autowired
    @Lazy
    private PluginService pluginService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            log.info("[{}] Processing {}", session.getId(), message);
            SessionMetaData sessionMd = internalSessionMap.get(session.getId());
            if (sessionMd != null) {
                //actorService.process(new TextPluginWebSocketMsg(sessionMd.sessionRef, message.getPayload()));
            } else {
                log.warn("[{}] Failed to find session", session.getId());
                session.close(CloseStatus.SERVER_ERROR.withReason("Session not found!"));
            }
            session.sendMessage(message);
        } catch (IOException e) {
            log.warn("IO error", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        try {
            String internalSessionId = session.getId();
            PluginWebsocketSessionRef sessionRef = null ; //toRef(session);
            String externalSessionId = sessionRef.getSessionId();
            internalSessionMap.put(internalSessionId, new SessionMetaData(session, sessionRef));
            externalSessionMap.put(externalSessionId, internalSessionId);
            //actorService.process(new SessionEventPluginWebSocketMsg(sessionRef, SessionEvent.onEstablished()));
            log.info("[{}][{}] Session is started", externalSessionId, session.getId());
        } catch (InvalidParameterException e) {
            log.warn("[[{}] Failed to start session", session.getId(), e);
            session.close(CloseStatus.BAD_DATA.withReason(e.getMessage()));
        } catch (Exception e) {
            log.warn("[{}] Failed to start session", session.getId(), e);
            session.close(CloseStatus.SERVER_ERROR.withReason(e.getMessage()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable tError) throws Exception {
        super.handleTransportError(session, tError);
        SessionMetaData sessionMd = internalSessionMap.get(session.getId());
        if (sessionMd != null) {
            processInActorService(new SessionEventPluginWebSocketMsg(sessionMd.sessionRef, SessionEvent.onError(tError)));
        } else {
            log.warn("[{}] Failed to find session", session.getId());
        }
        log.trace("[{}] Session transport error", session.getId(), tError);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        SessionMetaData sessionMd = internalSessionMap.remove(session.getId());
        if (sessionMd != null) {
            externalSessionMap.remove(sessionMd.sessionRef.getSessionId());
            processInActorService(new SessionEventPluginWebSocketMsg(sessionMd.sessionRef, SessionEvent.onClosed()));
        }
        log.info("[{}] Session is closed", session.getId());
    }

    private void processInActorService(SessionEventPluginWebSocketMsg msg) {
        try {
            //actorService.process(msg);
        } catch (BeanCreationNotAllowedException e) {
            log.warn("[{}] Failed to close session due to possible shutdown state", msg.getSessionRef().getSessionId());
        }
    }

   /* private PluginWebsocketSessionRef toRef(WebSocketSession session) throws IOException {
        URI sessionUri = session.getUri();
        String path = sessionUri.getPath();
        path = path.substring(WebSocketConfiguration.WS_PLUGIN_PREFIX.length());
        if (path.length() == 0) {
            throw new IllegalArgumentException("URL should contain plugin token!");
        }
        String[] pathElements = path.split("/");
        String pluginToken = pathElements[0];
        // TODO: cache
        PluginMetaData pluginMd = pluginService.findPluginByApiToken(pluginToken);
        if (pluginMd == null) {
            throw new InvalidParameterException("Can't find plugin with specified token!");
        } else {
            SecurityUser currentUser = (SecurityUser) session.getAttributes().get(WebSocketConfiguration.WS_SECURITY_USER_ATTRIBUTE);
            TenantId userId = currentUser.getUserId();
            UserId customerId = currentUser.getUserId();
            if (PluginApiController.validatePluginAccess(pluginMd, userId, customerId)) {
                PluginApiCallSecurityContext securityCtx = new PluginApiCallSecurityContext(pluginMd.getUserId(), pluginMd.getId(), userId,
                        currentUser.getUserId());
                return new BasicPluginWebsocketSessionRef(UUID.randomUUID().toString(), securityCtx, session.getUri(), session.getAttributes(),
                        session.getLocalAddress(), session.getRemoteAddress());
            } else {
                throw new SecurityException("Current user is not allowed to use this plugin!");
            }
        }
    }
*/
    private static class SessionMetaData {
        private final WebSocketSession session;
        private final PluginWebsocketSessionRef sessionRef;

        public SessionMetaData(WebSocketSession session, PluginWebsocketSessionRef sessionRef) {
            super();
            this.session = session;
            this.sessionRef = sessionRef;
        }
    }

    @Override
    public void send(PluginWebsocketMsg<?> wsMsg) throws IOException {
        PluginWebsocketSessionRef sessionRef = wsMsg.getSessionRef();
        String externalId = sessionRef.getSessionId();
        log.debug("[{}] Processing {}", externalId, wsMsg);
        String internalId = externalSessionMap.get(externalId);
        if (internalId != null) {
            SessionMetaData sessionMd = internalSessionMap.get(internalId);
            if (sessionMd != null) {
                if (wsMsg instanceof TextPluginWebSocketMsg) {
                    String payload = ((TextPluginWebSocketMsg) wsMsg).getPayload();
                    sessionMd.session.sendMessage(new TextMessage(payload));
                }
            } else {
                log.warn("[{}][{}] Failed to find session by internal id", externalId, internalId);
            }
        } else {
            log.warn("[{}] Failed to find session by external id", externalId);
        }
    }

    @Override
    public void close(PluginWebsocketSessionRef sessionRef) throws IOException {
        String externalId = sessionRef.getSessionId();
        log.debug("[{}] Processing close request", externalId);
        String internalId = externalSessionMap.get(externalId);
        if (internalId != null) {
            SessionMetaData sessionMd = internalSessionMap.get(internalId);
            if (sessionMd != null) {
                sessionMd.session.close(CloseStatus.NORMAL);
            } else {
                log.warn("[{}][{}] Failed to find session by internal id", externalId, internalId);
            }
        } else {
            log.warn("[{}] Failed to find session by external id", externalId);
        }
    }

}
