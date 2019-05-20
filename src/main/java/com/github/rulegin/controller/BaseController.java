
package com.github.rulegin.controller;

import com.github.rulegin.actors.service.ActorService;
import com.github.rulegin.actors.service.component.ComponentDiscoveryService;
import com.github.rulegin.actors.service.sercurity.SecurityUser;
import com.github.rulegin.common.data.Device;
import com.github.rulegin.common.data.User;
import com.github.rulegin.common.data.alarm.Alarm;
import com.github.rulegin.common.data.alarm.AlarmInfo;
import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.component.DefineComponent;
import com.github.rulegin.common.data.id.*;
import com.github.rulegin.common.data.page.TextPageLink;
import com.github.rulegin.common.data.page.TimePageLink;
import com.github.rulegin.common.data.plugin.PluginMetaData;
import com.github.rulegin.common.data.rule.RuleMetaData;
import com.github.rulegin.common.exception.RuleginErrorCode;
import com.github.rulegin.common.exception.RuleginErrorResponseHandler;
import com.github.rulegin.common.exception.RuleginException;
import com.github.rulegin.common.data.id.*;
import com.github.rulegin.dao.exception.DataValidationException;
import com.github.rulegin.dao.exception.IncorrectParameterException;
import com.github.rulegin.dao.model.ModelConstants;
import com.github.rulegin.dao.plugin.PluginService;
import com.github.rulegin.dao.relation.RelationService;
import com.github.rulegin.dao.rule.RuleService;
import com.github.rulegin.dao.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.github.rulegin.dao.util.Validator;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static com.github.rulegin.dao.util.Validator.validateId;


@Slf4j
public abstract class BaseController {

    @Autowired
    private RuleginErrorResponseHandler errorResponseHandler;
    //
    //@Autowired
    //protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ComponentDiscoveryService componentDescriptorService;

    @Autowired
    protected RuleService ruleService;

    @Autowired
    protected PluginService pluginService;

    @Autowired
    protected ActorService actorService;

    @Autowired
    protected RelationService relationService;


    @ExceptionHandler(RuleginException.class)
    public void handleRuleginException(RuleginException ex, HttpServletResponse response) {
        errorResponseHandler.handle(ex, response);
    }

    public RuleginException handleException(Exception exception) {
        return handleException(exception, true);
    }

    private RuleginException handleException(Exception exception, boolean logException) {
        if (logException) {
            log.error("Error [{}]", exception.getMessage());
        }

        String cause = "";
        if (exception.getCause() != null) {
            cause = exception.getCause().getClass().getCanonicalName();
        }

        if (exception instanceof RuleginException) {
            return (RuleginException) exception;
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
                || exception instanceof DataValidationException || cause.contains("IncorrectParameterException")) {
            return new RuleginException(exception.getMessage(), RuleginErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new RuleginException("Unable to send mail: " + exception.getMessage(), RuleginErrorCode.GENERAL);
        } else {
            return new RuleginException(exception.getMessage(), RuleginErrorCode.GENERAL);
        }
    }

    public <T> T checkNotNull(T reference) throws RuleginException {
        if (reference == null) {
            throw new RuleginException("Requested item wasn't found!", RuleginErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    <T> T checkNotNull(Optional<T> reference) throws RuleginException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new RuleginException("Requested item wasn't found!", RuleginErrorCode.ITEM_NOT_FOUND);
        }
    }

    public void checkParameter(String name, String param) throws RuleginException {
        if (StringUtils.isEmpty(param)) {
            throw new RuleginException("Parameter '" + name + "' can't be empty!", RuleginErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkArrayParameter(String name, String[] params) throws RuleginException {
        if (params == null || params.length == 0) {
            throw new RuleginException("Parameter '" + name + "' can't be empty!", RuleginErrorCode.BAD_REQUEST_PARAMS);
        } else {
            for (String param : params) {
                checkParameter(name, param);
            }
        }
    }

    public UUID toUUID(String id) {
        return UUID.fromString(id);
    }

    TimePageLink createPageLink(int limit, Long startTime, Long endTime, boolean ascOrder, String idOffset) {
        UUID idOffsetUuid = null;
        if (StringUtils.isNotEmpty(idOffset)) {
            idOffsetUuid = toUUID(idOffset);
        }
        return new TimePageLink(limit, startTime, endTime, ascOrder, idOffsetUuid);
    }


    public TextPageLink createPageLink(int limit, String textSearch, String idOffset, String textOffset) {
        UUID idOffsetUuid = null;
        if (StringUtils.isNotEmpty(idOffset)) {
            idOffsetUuid = toUUID(idOffset);
        }
        return new TextPageLink(limit, textSearch, idOffsetUuid, textOffset);
    }

    protected SecurityUser getCurrentUser() throws RuleginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
        if (true){
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new RuleginException("You aren't authorized to perform this operation!", RuleginErrorCode.AUTHENTICATION);
        }
    }

/*
    void checkUserId(UserId UserId) throws RuleginException {
        validateId(UserId, "Incorrect UserId " + UserId);
        SecurityUser authUser = getCurrentUser();
        if (authUser.getAuthority() != Authority.SYS_ADMIN &&
                (authUser.getUserId() == null || !authUser.getUserId().equals(UserId))) {
            throw new RuleginException("You don't have permission to perform this operation!",
                    RuleginErrorCode.PERMISSION_DENIED);
        }
    }*/



    User checkUserId(UserId userId) throws RuleginException {
        try {
            Validator.validateId(userId, "Incorrect userId " + userId);
            User user = userService.findUserById(userId);
            checkUser(user);
            return user;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    private void checkUser(User user) throws RuleginException {
        checkNotNull(user);
        checkUserId(user.getUserId());
    }

    protected void checkEntityId(EntityId entityId) throws RuleginException {
        try {
            checkNotNull(entityId);
            Validator.validateId(entityId.getId(), "Incorrect entityId " + entityId);
            switch (entityId.getEntityType()) {
               /* case CUSTOMER:
                    checkCustomerId(new UserId(entityId.getId()));
                    return;
                case TENANT:
                    checkUserId(new UserId(entityId.getId()));
                    return;*/
                case PLUGIN:
                    checkPlugin(new PluginId(entityId.getId()));
                    return;
                case RULE:
                    checkRule(new RuleId(entityId.getId()));
                    return;
                /*case ASSET:
                    checkAsset(assetService.findAssetById(new AssetId(entityId.getId())));
                    return;
                case DASHBOARD:
                    checkDashboardId(new DashboardId(entityId.getId()));
                    return;*/
                case USER:
                    checkUserId(new UserId(entityId.getId()));
                    return;
                default:
                    throw new IllegalArgumentException("Unsupported entity type: " + entityId.getEntityType());
            }
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }


    protected void checkDevice(Device device) throws RuleginException {
        checkNotNull(device);
        checkUserId(device.getUserId());
        //if (device.getUserId() != null && !device.getUserId().getId().equals(ModelConstants.NULL_UUID)) {
        //    checkCustomerId(device.getUserId());
        //}
    }

/*    Asset checkAssetId(AssetId assetId) throws RuleginException {
        try {
            validateId(assetId, "Incorrect assetId " + assetId);
            Asset asset = assetService.findAssetById(assetId);
            checkAsset(asset);
            return asset;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected void checkAsset(Asset asset) throws RuleginException {
        checkNotNull(asset);
        checkUserId(asset.getUserId());
        if (asset.getUserId() != null && !asset.getUserId().getId().equals(ModelConstants.NULL_UUID)) {
            checkCustomerId(asset.getUserId());
        }
    }*/

    Alarm checkAlarmId(AlarmId alarmId) throws RuleginException {
        try {
            Validator.validateId(alarmId, "Incorrect alarmId " + alarmId);
            Alarm alarm = null ;//alarmService.findAlarmByIdAsync(alarmId).get();
            checkAlarm(alarm);
            return alarm;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    AlarmInfo checkAlarmInfoId(AlarmId alarmId) throws RuleginException {
        try {
            Validator.validateId(alarmId, "Incorrect alarmId " + alarmId);
            AlarmInfo alarmInfo = null ;// alarmService.findAlarmInfoByIdAsync(alarmId).get();
            checkAlarm(alarmInfo);
            return alarmInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected void checkAlarm(Alarm alarm) throws RuleginException {
        checkNotNull(alarm);
        //checkUserId(alarm.getUserId());
    }

    DefineComponent checkComponentDescriptorByClazz(String clazz) throws RuleginException {
        try {
            log.debug("[{}] Lookup component descriptor", clazz);
            DefineComponent componentDescriptor = checkNotNull(componentDescriptorService.getComponent(clazz));
            return componentDescriptor;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<DefineComponent> checkComponentDescriptorsByType(ComponentType type) throws RuleginException {
        try {
            log.debug("[{}] Lookup component descriptors", type);
            return componentDescriptorService.getComponents(type);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<DefineComponent> checkPluginActionsByPluginClazz(String pluginClazz) throws RuleginException {
        try {
            checkComponentDescriptorByClazz(pluginClazz);
            log.debug("[{}] Lookup plugin actionsType", pluginClazz);
            return componentDescriptorService.getPluginActions(pluginClazz);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected PluginMetaData checkPlugin(PluginMetaData plugin) throws RuleginException {
        checkNotNull(plugin);
        SecurityUser authUser = getCurrentUser();
        UserId UserId = plugin.getUserId();
        Validator.validateId(UserId, "Incorrect UserId " + UserId);
       // if (authUser.getAuthority() != Authority.SYS_ADMIN) {
            if (authUser.getUserId() == null ||
                    !UserId.getId().equals(ModelConstants.NULL_UUID) && !authUser.getUserId().equals(UserId)) {
                throw new RuleginException("You don't have permission to perform this operation!",
                        RuleginErrorCode.PERMISSION_DENIED);

            } else if (UserId.getId().equals(ModelConstants.NULL_UUID)) {
                plugin.setConfiguration(null);
            }
       // }
        return plugin;
    }

    protected PluginMetaData checkPlugin(PluginId pluginId) throws RuleginException {
        checkNotNull(pluginId);
        return checkPlugin(pluginService.findPluginById(pluginId));
    }

    protected RuleMetaData checkRule(RuleId ruleId) throws RuleginException {
        checkNotNull(ruleId);
        return checkRule(ruleService.findRuleById(ruleId));
    }

    protected RuleMetaData checkRule(RuleMetaData rule) throws RuleginException {
        checkNotNull(rule);
        //SecurityUser authUser = getCurrentUser();
        UserId UserId = rule.getUserId();
        Validator.validateId(UserId, "Incorrect UserId " + UserId);
     /*   if (authUser.getAuthority() != Authority.SYS_ADMIN) {
            if (authUser.getUserId() == null ||
                    !UserId.getId().equals(ModelConstants.NULL_UUID) && !authUser.getUserId().equals(UserId)) {
                throw new RuleginException("You don't have permission to perform this operation!",
                        RuleginErrorCode.PERMISSION_DENIED);

            }
        }*/
        return rule;
    }

    protected String constructBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        if (request.getHeader("x-forwarded-proto") != null) {
            scheme = request.getHeader("x-forwarded-proto");
        }
        int serverPort = request.getServerPort();
        if (request.getHeader("x-forwarded-port") != null) {
            try {
                serverPort = request.getIntHeader("x-forwarded-port");
            } catch (NumberFormatException e) {
            }
        }

        String baseUrl = String.format("%s://%s:%d",
                scheme,
                request.getServerName(),
                serverPort);
        return baseUrl;
    }
}
