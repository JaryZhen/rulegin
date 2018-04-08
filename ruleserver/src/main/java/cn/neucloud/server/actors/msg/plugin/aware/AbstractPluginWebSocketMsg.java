
package cn.neucloud.server.actors.msg.plugin.aware;

import cn.neucloud.server.actors.msg.ws.PluginWebsocketSessionRef;
import cn.neucloud.server.core.action.plugins.PluginApiCallSecurityContext;
import cn.neucloud.server.common.data.id.PluginId;

public abstract class AbstractPluginWebSocketMsg<T> implements PluginWebsocketMsg<T> {

    private static final long serialVersionUID = 1L;

    private final PluginWebsocketSessionRef sessionRef;
    private final T payload;

    AbstractPluginWebSocketMsg(PluginWebsocketSessionRef sessionRef, T payload) {
        this.sessionRef = sessionRef;
        this.payload = payload;
    }

    public PluginWebsocketSessionRef getSessionRef() {
        return sessionRef;
    }


    @Override
    public PluginApiCallSecurityContext getSecurityCtx() {
        return sessionRef.getSecurityCtx();
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "AbstractPluginWebSocketMsg [sessionRef=" + sessionRef + ", payload=" + payload + "]";
    }
    @Override
    public PluginId getPluginId() {
        return sessionRef.getPluginId();
    }
}
