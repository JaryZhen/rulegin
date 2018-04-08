package cn.neucloud.server.controller;

import cn.neucloud.server.common.data.component.ComponentLifecycleEvent;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.common.data.page.TextPageData;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.common.data.plugin.PluginMetaData;
import cn.neucloud.server.common.data.rule.RuleMetaData;
import cn.neucloud.server.common.exception.NeuruleException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RuleController extends BaseController {

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule", method = RequestMethod.POST)
    @ResponseBody
    public UUID saveRule(@RequestBody RuleMetaData source) throws NeuruleException {
        try {
            boolean created = source.getId() == null;
            //source.setUserId(getCurrentUser().getUserId());
            source.setUserId(new UserId(UUID.fromString("13814000-1dd2-11b2-8080-808080808080")));
            RuleMetaData rule = checkNotNull(ruleService.saveRule(source));
            actorService.onRuleStateChange(rule.getUserId(), rule.getId(),
                    created ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);
            return rule.getId().getId();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void activateRuleById(@PathVariable("ruleId") String strRuleId) throws NeuruleException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            RuleMetaData rule = checkRule(ruleService.findRuleById(ruleId));
            //更新rule的生命周期、确认rule pluge的状态以及rule有action pluge
            ruleService.activateRuleById(ruleId);
            //发送actor
            actorService.onRuleStateChange(rule.getUserId(), rule.getId(), ComponentLifecycleEvent.ACTIVATED);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}/suspend", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void suspendRuleById(@PathVariable("ruleId") String strRuleId) throws NeuruleException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            RuleMetaData rule = checkRule(ruleService.findRuleById(ruleId));
            ruleService.suspendRuleById(ruleId);
            actorService.onRuleStateChange(rule.getUserId(), rule.getId(), ComponentLifecycleEvent.SUSPENDED);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}", method = RequestMethod.GET)
    @ResponseBody
    public RuleMetaData getRuleById(@PathVariable("ruleId") String strRuleId) throws NeuruleException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            return checkRule(ruleService.findRuleById(ruleId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/token/{pluginToken}", method = RequestMethod.GET)
    @ResponseBody
    public List<RuleMetaData> getRulesByPluginToken(@PathVariable("pluginToken") String pluginToken) throws NeuruleException {
        checkParameter("pluginToken", pluginToken);
        try {
            PluginMetaData plugin = checkPlugin(pluginService.findPluginByApiToken(pluginToken));
            return ruleService.findPluginRules(plugin.getApiToken());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/rule/system", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<RuleMetaData> getSystemRules(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws NeuruleException {
        try {
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(ruleService.findSystemRules(pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    @ResponseBody
    public List<RuleMetaData> getRules() throws NeuruleException {
        try {
            //if (getCurrentUser().getAuthority() == Authority.SYS_ADMIN) {
            //    return checkNotNull(ruleService.findSystemRules());
            //} else {
            //    UserId userId = getCurrentUser().getUserId();
            //    return checkNotNull(ruleService.findAllUserRulesByUserId(userId));
            //}
            UserId userId = getCurrentUser().getUserId();
            return checkNotNull(ruleService.findAllUserRulesByUserId(userId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/rule", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<RuleMetaData> getTenantRules(
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws NeuruleException {
        try {
            UserId tenantId = getCurrentUser().getUserId();
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(ruleService.findTenantRules(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/rule/{ruleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteRule(@PathVariable("ruleId") String strRuleId) throws NeuruleException {
        checkParameter("ruleId", strRuleId);
        try {
            RuleId ruleId = new RuleId(toUUID(strRuleId));
            RuleMetaData rule = checkRule(ruleService.findRuleById(ruleId));
            ruleService.deleteRuleById(ruleId);
            actorService.onRuleStateChange(rule.getUserId(), rule.getId(), ComponentLifecycleEvent.DELETED);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
