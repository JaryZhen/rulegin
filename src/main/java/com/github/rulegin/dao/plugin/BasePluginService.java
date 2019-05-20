package com.github.rulegin.dao.plugin;

import com.github.rulegin.common.data.component.ComponentLifecycleState;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.id.UUIDBased;
import com.github.rulegin.common.data.id.UserId;
import com.github.rulegin.common.data.page.TextPageData;
import com.github.rulegin.common.data.page.TextPageLink;
import com.github.rulegin.common.data.plugin.PluginMetaData;
import com.github.rulegin.common.data.rule.RuleMetaData;
import com.github.rulegin.dao.entity.AbstractEntityService;
import com.github.rulegin.dao.model.ModelConstants;
import com.github.rulegin.dao.rule.RuleDao;
import com.github.rulegin.dao.util.DataValidator;
import com.github.rulegin.dao.util.Validator;
import com.github.rulegin.dao.component.ComponentDescriptorService;
import com.github.rulegin.dao.exception.DataValidationException;
import com.github.rulegin.dao.exception.DatabaseException;
import com.github.rulegin.dao.exception.IncorrectParameterException;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.rulegin.dao.util.Validator.validateId;

@Service
@Slf4j
public class BasePluginService extends AbstractEntityService implements PluginService {

    //TODO: move to a better place.
    public static final UserId SYSTEM_TENANT = new UserId(ModelConstants.NULL_UUID);

    @Autowired
    private PluginDao pluginDao;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ComponentDescriptorService componentDescriptorService;

    private DataValidator<PluginMetaData> pluginValidator =
            new DataValidator<PluginMetaData>() {
                @Override
                protected void validateDataImpl(PluginMetaData plugin) {
                    if (StringUtils.isEmpty(plugin.getName())) {
                        throw new DataValidationException("Plugin name should be specified!.");
                    }
                    if (StringUtils.isEmpty(plugin.getClazz())) {
                        throw new DataValidationException("Plugin clazz should be specified!.");
                    }
                    if (StringUtils.isEmpty(plugin.getApiToken())) {
                        throw new DataValidationException("Plugin api token is not set!");
                    }
                    if (plugin.getConfiguration() == null) {
                        throw new DataValidationException("Plugin configuration is not set!");
                    }
                }

                @Override
                protected void validateCreate(PluginMetaData data) {
                    //log.info("creating .....");
                    //TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
                    //UUID uuid = gen.generate();
                    //PluginId pluginId = new PluginId(uuid);
                    //dataSource.setId(pluginId);
                }

                @Override
                public void validateUpdate(PluginMetaData data) {

                    UUIDBased id = new UUIDBased() {
                        @Override
                        public UUID getId() {
                            return super.getId();
                        }
                    };
                    PluginId pluginId = new PluginId(id.getId());
                    data.setId(pluginId);
                }

            };

    @Override
    public PluginMetaData savePlugin(PluginMetaData plugin) {
        pluginValidator.validate(plugin);

        if (plugin.getUserId() == null) {
            log.trace("Save system plugin metadata with predefined id {}", SYSTEM_TENANT);
            plugin.setUserId(SYSTEM_TENANT);
        }
        if (plugin.getId() != null) {
            PluginMetaData oldVersion = pluginDao.findById(plugin.getId());
                if (plugin.getState() == null) {
                    plugin.setState(oldVersion.getState());
                } else if (plugin.getState() != oldVersion.getState()) {
                    throw new IncorrectParameterException("Use Activate/Suspend method to control state of the plugin!");
                }

        } else {
            if (plugin.getState() == null) {
                plugin.setState(ComponentLifecycleState.SUSPENDED);
            } else if (plugin.getState() != ComponentLifecycleState.SUSPENDED) {
                throw new IncorrectParameterException("Use Activate/Suspend method to control state of the plugin!");
            }
        }

      /*  ComponentDescriptor descriptor = componentDescriptorService.findByClazz(plugin.getClazz());
        if (descriptor == null) {
            throw new IncorrectParameterException("Plugin descriptor not found!");
        } else if (!ComponentType.PLUGIN.equals(descriptor.getType())) {
            throw new IncorrectParameterException("Plugin class is actually " + descriptor.getType() + "!");
        }
        */
        PluginMetaData savedPlugin = pluginDao.findByApiToken(plugin.getApiToken());
        if (savedPlugin != null && (plugin.getId() == null || !savedPlugin.getId().getId().equals(plugin.getId().getId()))) {
            throw new IncorrectParameterException("API token is already reserved!");
        }
       /* if (!componentDescriptorService.validate(descriptor, plugin.getConfiguration())) {
            throw new IncorrectParameterException("Filters configuration is not valid!");
        }*/
        return pluginDao.save(plugin);
    }

    @Override
    public List<PluginMetaData> findAll() {
        return pluginDao.findSystemPlugin();
    }

    @Override
    public PluginMetaData findPluginById(PluginId pluginId) {
        Validator.validateId(pluginId, "Incorrect plugin id for search request.");
        return pluginDao.findById(pluginId);
    }

    @Override
    public ListenableFuture<PluginMetaData> findPluginByIdAsync(PluginId pluginId) {
        Validator.validateId(pluginId, "Incorrect plugin id for search plugin request.");
        return pluginDao.findByIdAsync(pluginId.getId());
    }

    @Override
    public PluginMetaData findPluginByApiToken(String apiToken) {
        Validator.validateString(apiToken, "Incorrect plugin apiToken for search request.");
        return pluginDao.findByApiToken(apiToken);
    }

    @Override
    public TextPageData<PluginMetaData> findSystemPlugins(TextPageLink pageLink) {
        Validator.validatePageLink(pageLink, "Incorrect PageLink object for search system plugin request.");
        List<PluginMetaData> plugins = pluginDao.findSystemPlugin();//pluginDao.findByTenantIdAndPageLink(SYSTEM_TENANT, pageLink);
        return new TextPageData<>(plugins, pageLink);
    }

    @Override
    public TextPageData<PluginMetaData> findTenantPlugins(UserId tenantId, TextPageLink pageLink) {
        return null;
    }


    @Override
    public List<PluginMetaData> findSystemPlugins() {
        log.trace("Executing findSystemPlugins");
        List<PluginMetaData> plugins = new ArrayList<>();
        TextPageLink pageLink = new TextPageLink(300);
        TextPageData<PluginMetaData> pageData = null;
        do {
            pageData = findSystemPlugins(pageLink);
            plugins.addAll(pageData.getData());
            if (pageData.hasNext()) {
                pageLink = pageData.getNextPageLink();
            }
        } while (pageData.hasNext());
        return plugins;
    }

    @Override
    public void activatePluginById(PluginId pluginId) {
        updateLifeCycleState(pluginId, ComponentLifecycleState.ACTIVE);
    }

    @Override
    public void suspendPluginById(PluginId pluginId) {
        PluginMetaData plugin = pluginDao.findById(pluginId);
        List<RuleMetaData> affectedRules = ruleDao.findRulesByPlugin(plugin.getApiToken())
                .stream().filter(rule -> rule.getState() == ComponentLifecycleState.ACTIVE).collect(Collectors.toList());
        if (affectedRules.isEmpty()) {
            updateLifeCycleState(pluginId, ComponentLifecycleState.SUSPENDED);
        } else {
            throw new DataValidationException("Can't suspend plugin that has active rules!");
        }
    }

    private void updateLifeCycleState(PluginId pluginId, ComponentLifecycleState state) {
        Validator.validateId(pluginId, "Incorrect plugin id for state change request.");
        PluginMetaData plugin = pluginDao.findById(pluginId);
        if (plugin != null) {
            plugin.setState(state);
            pluginDao.save(plugin);
        } else {
            throw new DatabaseException("Plugin not found!");
        }
    }

    @Override
    public void deletePluginById(PluginId pluginId) {
        Validator.validateId(pluginId, "Incorrect plugin id for delete request.");
        deleteEntityRelations(pluginId);
        checkRulesAndDelete(pluginId.getId());
    }

    private void checkRulesAndDelete(UUID pluginId) {
        PluginMetaData plugin = pluginDao.findById(pluginId);
        List<RuleMetaData> affectedRules = ruleDao.findRulesByPlugin(plugin.getApiToken());
        if (affectedRules.isEmpty()) {
            pluginDao.deleteById(pluginId);
        } else {
            throw new DataValidationException("Plugin deletion will affect existing rules!");
        }
    }
}
