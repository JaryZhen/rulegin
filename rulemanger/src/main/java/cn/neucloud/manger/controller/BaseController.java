
package cn.neucloud.manger.controller;

import cn.neucloud.server.actors.service.ActorService;
import cn.neucloud.server.actors.service.component.ComponentDiscoveryService;
import cn.neucloud.server.actors.service.sercurity.SecurityUser;
import cn.neucloud.server.common.data.Device;
import cn.neucloud.server.common.data.User;
import cn.neucloud.server.common.data.alarm.Alarm;
import cn.neucloud.server.common.data.alarm.AlarmId;
import cn.neucloud.server.common.data.alarm.AlarmInfo;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.component.DefineComponent;
import cn.neucloud.server.common.data.id.EntityId;
import cn.neucloud.server.common.data.id.PluginId;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.common.data.page.TimePageLink;
import cn.neucloud.server.common.data.plugin.PluginMetaData;
import cn.neucloud.server.common.data.rule.RuleMetaData;
import cn.neucloud.server.common.exception.NeuruleErrorCode;
import cn.neucloud.server.common.exception.NeuruleErrorResponseHandler;
import cn.neucloud.server.common.exception.NeuruleException;
import cn.neucloud.server.dao.exception.DataValidationException;
import cn.neucloud.server.dao.exception.IncorrectParameterException;
import cn.neucloud.server.dao.model.ModelConstants;
import cn.neucloud.server.dao.plugin.PluginService;
import cn.neucloud.server.dao.relation.RelationService;
import cn.neucloud.server.dao.rule.RuleService;
import cn.neucloud.server.dao.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static cn.neucloud.server.dao.util.Validator.validateId;


@Slf4j
public abstract class BaseController {

    @Autowired
    private NeuruleErrorResponseHandler errorResponseHandler;
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


    @ExceptionHandler(NeuruleException.class)
    public void handleNeuruleException(NeuruleException ex, HttpServletResponse response) {
        errorResponseHandler.handle(ex, response);
    }

    public  NeuruleException handleException(Exception exception) {
        return handleException(exception, true);
    }

    private NeuruleException handleException(Exception exception, boolean logException) {
        if (logException) {
            log.error("Error [{}]", exception.getMessage());
        }

        String cause = "";
        if (exception.getCause() != null) {
            cause = exception.getCause().getClass().getCanonicalName();
        }

        if (exception instanceof NeuruleException) {
            return (NeuruleException) exception;
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
                || exception instanceof DataValidationException || cause.contains("IncorrectParameterException")) {
            return new NeuruleException(exception.getMessage(), NeuruleErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new NeuruleException("Unable to send mail: " + exception.getMessage(), NeuruleErrorCode.GENERAL);
        } else {
            return new NeuruleException(exception.getMessage(), NeuruleErrorCode.GENERAL);
        }
    }

    public <T> T checkNotNull(T reference) throws NeuruleException {
        if (reference == null) {
            throw new NeuruleException("Requested item wasn't found!", NeuruleErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    <T> T checkNotNull(Optional<T> reference) throws NeuruleException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new NeuruleException("Requested item wasn't found!", NeuruleErrorCode.ITEM_NOT_FOUND);
        }
    }

    public void checkParameter(String name, String param) throws NeuruleException {
        if (StringUtils.isEmpty(param)) {
            throw new NeuruleException("Parameter '" + name + "' can't be empty!", NeuruleErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkArrayParameter(String name, String[] params) throws NeuruleException {
        if (params == null || params.length == 0) {
            throw new NeuruleException("Parameter '" + name + "' can't be empty!", NeuruleErrorCode.BAD_REQUEST_PARAMS);
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

    protected SecurityUser getCurrentUser() throws NeuruleException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
        if (true){
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new NeuruleException("You aren't authorized to perform this operation!", NeuruleErrorCode.AUTHENTICATION);
        }
    }

/*
    void checkUserId(UserId UserId) throws NeuruleException {
        validateId(UserId, "Incorrect UserId " + UserId);
        SecurityUser authUser = getCurrentUser();
        if (authUser.getAuthority() != Authority.SYS_ADMIN &&
                (authUser.getUserId() == null || !authUser.getUserId().equals(UserId))) {
            throw new NeuruleException("You don't have permission to perform this operation!",
                    NeuruleErrorCode.PERMISSION_DENIED);
        }
    }*/



    User checkUserId(UserId userId) throws NeuruleException {
        try {
            validateId(userId, "Incorrect userId " + userId);
            User user = userService.findUserById(userId);
            checkUser(user);
            return user;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    private void checkUser(User user) throws NeuruleException {
        checkNotNull(user);
        checkUserId(user.getUserId());
    }

    protected void checkEntityId(EntityId entityId) throws NeuruleException {
        try {
            checkNotNull(entityId);
            validateId(entityId.getId(), "Incorrect entityId " + entityId);
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


    protected void checkDevice(Device device) throws NeuruleException {
        checkNotNull(device);
        checkUserId(device.getUserId());
        //if (device.getUserId() != null && !device.getUserId().getId().equals(ModelConstants.NULL_UUID)) {
        //    checkCustomerId(device.getUserId());
        //}
    }

/*    Asset checkAssetId(AssetId assetId) throws NeuruleException {
        try {
            validateId(assetId, "Incorrect assetId " + assetId);
            Asset asset = assetService.findAssetById(assetId);
            checkAsset(asset);
            return asset;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected void checkAsset(Asset asset) throws NeuruleException {
        checkNotNull(asset);
        checkUserId(asset.getUserId());
        if (asset.getUserId() != null && !asset.getUserId().getId().equals(ModelConstants.NULL_UUID)) {
            checkCustomerId(asset.getUserId());
        }
    }*/

    Alarm checkAlarmId(AlarmId alarmId) throws NeuruleException {
        try {
            validateId(alarmId, "Incorrect alarmId " + alarmId);
            Alarm alarm = null ;//alarmService.findAlarmByIdAsync(alarmId).get();
            checkAlarm(alarm);
            return alarm;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    AlarmInfo checkAlarmInfoId(AlarmId alarmId) throws NeuruleException {
        try {
            validateId(alarmId, "Incorrect alarmId " + alarmId);
            AlarmInfo alarmInfo = null ;// alarmService.findAlarmInfoByIdAsync(alarmId).get();
            checkAlarm(alarmInfo);
            return alarmInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected void checkAlarm(Alarm alarm) throws NeuruleException {
        checkNotNull(alarm);
        //checkUserId(alarm.getUserId());
    }

    DefineComponent checkComponentDescriptorByClazz(String clazz) throws NeuruleException {
        try {
            log.debug("[{}] Lookup component descriptor", clazz);
            DefineComponent componentDescriptor = checkNotNull(componentDescriptorService.getComponent(clazz));
            return componentDescriptor;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<DefineComponent> checkComponentDescriptorsByType(ComponentType type) throws NeuruleException {
        try {
            log.debug("[{}] Lookup component descriptors", type);
            return componentDescriptorService.getComponents(type);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<DefineComponent> checkPluginActionsByPluginClazz(String pluginClazz) throws NeuruleException {
        try {
            checkComponentDescriptorByClazz(pluginClazz);
            log.debug("[{}] Lookup plugin actionsType", pluginClazz);
            return componentDescriptorService.getPluginActions(pluginClazz);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected PluginMetaData checkPlugin(PluginMetaData plugin) throws NeuruleException {
        checkNotNull(plugin);
        SecurityUser authUser = getCurrentUser();
        UserId UserId = plugin.getUserId();
        validateId(UserId, "Incorrect UserId " + UserId);
       // if (authUser.getAuthority() != Authority.SYS_ADMIN) {
            if (authUser.getUserId() == null ||
                    !UserId.getId().equals(ModelConstants.NULL_UUID) && !authUser.getUserId().equals(UserId)) {
                throw new NeuruleException("You don't have permission to perform this operation!",
                        NeuruleErrorCode.PERMISSION_DENIED);

            } else if (UserId.getId().equals(ModelConstants.NULL_UUID)) {
                plugin.setConfiguration(null);
            }
       // }
        return plugin;
    }

    protected PluginMetaData checkPlugin(PluginId pluginId) throws NeuruleException {
        checkNotNull(pluginId);
        return checkPlugin(pluginService.findPluginById(pluginId));
    }

    protected RuleMetaData checkRule(RuleId ruleId) throws NeuruleException {
        checkNotNull(ruleId);
        return checkRule(ruleService.findRuleById(ruleId));
    }

    protected RuleMetaData checkRule(RuleMetaData rule) throws NeuruleException {
        checkNotNull(rule);
        //SecurityUser authUser = getCurrentUser();
        UserId UserId = rule.getUserId();
        validateId(UserId, "Incorrect UserId " + UserId);
     /*   if (authUser.getAuthority() != Authority.SYS_ADMIN) {
            if (authUser.getUserId() == null ||
                    !UserId.getId().equals(ModelConstants.NULL_UUID) && !authUser.getUserId().equals(UserId)) {
                throw new NeuruleException("You don't have permission to perform this operation!",
                        NeuruleErrorCode.PERMISSION_DENIED);

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
