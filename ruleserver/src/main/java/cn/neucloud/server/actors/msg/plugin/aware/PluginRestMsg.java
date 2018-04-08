
package cn.neucloud.server.actors.msg.plugin.aware;

import cn.neucloud.server.actors.msg.RestRequest;
import cn.neucloud.server.core.action.plugins.PluginApiCallSecurityContext;
import cn.neucloud.server.common.data.id.PluginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
@SuppressWarnings("rawtypes")
public interface PluginRestMsg extends ToPluginActorMsg {

    RestRequest getRequest();

    DeferredResult<ResponseEntity> getResponseHolder();
    
    PluginApiCallSecurityContext getSecurityCtx();
    
    @Override
    default PluginId getPluginId() {
        return getSecurityCtx().getPluginId();
    }

}
