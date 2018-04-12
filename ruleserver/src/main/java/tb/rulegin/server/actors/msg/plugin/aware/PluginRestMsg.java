
package tb.rulegin.server.actors.msg.plugin.aware;

import tb.rulegin.server.actors.msg.RestRequest;
import tb.rulegin.server.core.action.plugins.PluginApiCallSecurityContext;
import tb.rulegin.server.common.data.id.PluginId;
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
