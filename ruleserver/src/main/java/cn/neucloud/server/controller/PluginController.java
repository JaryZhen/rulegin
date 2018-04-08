/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.neucloud.server.controller;

import cn.neucloud.server.common.data.UUIDConverter;
import cn.neucloud.server.common.data.component.ComponentLifecycleEvent;
import cn.neucloud.server.common.data.id.PluginId;
import cn.neucloud.server.common.data.page.TextPageData;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.common.data.plugin.PluginMetaData;
import cn.neucloud.server.common.exception.NeuruleException;
import cn.neucloud.server.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class PluginController extends BaseController {

    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.GET)
    @ResponseBody
    public PluginMetaData getPluginById(@PathVariable("pluginId") String strPluginId) throws NeuruleException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            return pluginService.findPluginById(pluginId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping(value = "/plugin/token/{pluginToken}", method = RequestMethod.GET)
    @ResponseBody
    public PluginMetaData getPluginByToken(@PathVariable("pluginToken") String pluginToken) throws NeuruleException {
        checkParameter("pluginToken", pluginToken);
        try {
            return pluginService.findPluginByApiToken(pluginToken);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping(value = "/plugin", method = RequestMethod.POST)
    @ResponseBody
    public PluginMetaData savePlugin(@RequestBody PluginMetaData source) throws NeuruleException {
        log.info("pluginMetadata "+source.toString());

        try {
            boolean created = source.getId() == null;
            log.info("create = "+created);
            PluginMetaData plugin = pluginService.savePlugin(source);

            log.info(source.toString());
            actorService.onPluginStateChange(plugin.getUserId(), plugin.getId(), created ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);
            return plugin;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping(value = "/plugin/{pluginId}/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void activatePluginById(@PathVariable("pluginId") String strPluginId) throws NeuruleException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            PluginMetaData plugin = pluginService.findPluginById(pluginId);
            pluginService.activatePluginById(pluginId);
            actorService.onPluginStateChange( plugin.getUserId(),plugin.getId(), ComponentLifecycleEvent.ACTIVATED);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping(value = "/plugin/{pluginId}/suspend", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void suspendPluginById(@PathVariable("pluginId") String strPluginId) throws NeuruleException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            PluginMetaData plugin = (pluginService.findPluginById(pluginId));
            pluginService.suspendPluginById(pluginId);
            actorService.onPluginStateChange(plugin.getUserId(),plugin.getId(), ComponentLifecycleEvent.SUSPENDED);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping(value = "/plugin/system", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<PluginMetaData> getSystemPlugins(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws NeuruleException {
        try {
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return (pluginService.findSystemPlugins(pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    //@PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePlugin(@PathVariable("pluginId") String strPluginId) throws NeuruleException {
        checkParameter("pluginId", strPluginId);
        try {
            PluginId pluginId = new PluginId(toUUID(strPluginId));
            PluginMetaData plugin = (pluginService.findPluginById(pluginId));
            pluginService.deletePluginById(pluginId);
            actorService.onPluginStateChange( plugin.getUserId(),plugin.getId(), ComponentLifecycleEvent.DELETED);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @RequestMapping("/test")
    public String test() throws Exception {
        log.info("Hello "+"  start Spring Boot");

        return "----";
    }
}
