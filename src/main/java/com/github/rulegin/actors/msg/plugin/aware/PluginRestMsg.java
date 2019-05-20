
package com.github.rulegin.actors.msg.plugin.aware;

import com.github.rulegin.actors.msg.RestRequest;
import com.github.rulegin.core.action.plugins.PluginApiCallSecurityContext;
import com.github.rulegin.common.data.id.PluginId;
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
