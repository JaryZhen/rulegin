package cn.neucloud.server.dao.rule;

import cn.neucloud.server.common.data.component.ComponentLifecycleState;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.common.data.page.TextPageData;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.common.data.rule.RuleMetaData;
import cn.neucloud.server.dao.components.DefineComponentService;
import cn.neucloud.server.dao.entity.AbstractEntityService;
import cn.neucloud.server.dao.exception.DataValidationException;
import cn.neucloud.server.dao.exception.DatabaseException;
import cn.neucloud.server.dao.exception.IncorrectParameterException;
import cn.neucloud.server.dao.plugin.PluginService;
import cn.neucloud.server.dao.util.DataValidator;
import cn.neucloud.server.dao.util.Validator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static cn.neucloud.server.dao.util.Validator.validateId;
import static cn.neucloud.server.dao.util.Validator.validatePageLink;

@Service
@Slf4j
public class BaseRuleService extends AbstractEntityService implements RuleService {


    @Autowired
    public RuleDao ruleDao;

    @Autowired
    public PluginService pluginService;

    @Autowired
    private DefineComponentService componentService;

    private DataValidator<RuleMetaData> ruleValidator =
            new DataValidator<RuleMetaData>() {
                @Override
                protected void validateDataImpl(RuleMetaData rule) {
                    if (StringUtils.isEmpty(rule.getName())) {
                        throw new DataValidationException("Rule name should be specified!.");
                    }
                }
            };

    @Override
    public RuleMetaData saveRule(RuleMetaData rule) {
        ruleValidator.validate(rule);
        if (rule.getId() != null) {
            RuleMetaData oldVersion = ruleDao.findById(rule.getId());
            if (rule.getState() == null) {
                rule.setState(oldVersion.getState());
            } else if (rule.getState() != oldVersion.getState()) {
                throw new IncorrectParameterException("Use Activate/Suspend method to control state of the Rule!");
            }
        } else {
            if (rule.getState() == null) {
                rule.setState(ComponentLifecycleState.SUSPENDED);
            } else if (rule.getState() != ComponentLifecycleState.SUSPENDED) {
                throw new IncorrectParameterException("Use Activate/Suspend method to control state of the Rule!");
            }
        }
        validateDataSource(rule.getDataSource());
        validateFilters(rule.getFilters());
        validateActions(rule.getActions());

        //validateRuleAndPluginState(Rule);
        return ruleDao.save(rule);
    }

    @Override
    public List<RuleMetaData> findAll() {
        return ruleDao.findSystemRules();

    }

    private void validateDataSource(JsonNode filtersJson) {
        if (filtersJson == null || filtersJson.isNull()) {
            throw new IncorrectParameterException("Rule dataSource are required!");
        }
        validateDataSourceOrFilterJson(filtersJson, ComponentType.RULE);
    }

    private void validateFilters(JsonNode filtersJson) {
        if (filtersJson == null || filtersJson.isNull()) {
            throw new IncorrectParameterException("Rule filters are required!");
        }
        if (!filtersJson.isArray()) {
            throw new IncorrectParameterException("Filters json is not an array!");
        }
        ArrayNode filtersArray = (ArrayNode) filtersJson;
        for (int i = 0; i < filtersArray.size(); i++) {
            validateDataSourceOrFilterJson(filtersArray.get(i), ComponentType.FILTER);
        }
    }

/*
    private void validateComponentJson(JsonNode json, ComponentType type) {
        if (json == null || json.isNull()) {
            throw new IncorrectParameterException(type.name() + " is required!");
        }
        String clazz = getIfValid(type.name(), json, "clazz", JsonNode::isTextual, JsonNode::asText);
        //String name = getIfValid(type.name(), json, "name", JsonNode::isTextual, JsonNode::asText);
        JsonNode configuration = getIfValid(type.name(), json, "configuration", JsonNode::isObject, node -> node);
        ComponentDescriptor descriptor = componentDescriptorService.findByClazz(clazz);
        if (descriptor == null) {
            throw new IncorrectParameterException(type.name() + " clazz " + clazz + " is not a valid component!");
        }
        if (descriptor.getType() != type) {
            throw new IncorrectParameterException("Clazz " + clazz + " is not a valid " + type.name() + " component!");
        }
        if (!componentDescriptorService.validate(descriptor, configuration)) {
            throw new IncorrectParameterException(type.name() + " configuration is not valid!");
        }
    }
*/

    private void validateActions(JsonNode filtersJson) {
        if (filtersJson == null || filtersJson.isNull()) {
            throw new IncorrectParameterException("Rule actionsType are required!");
        }
        if (!filtersJson.isArray()) {
            throw new IncorrectParameterException("Action json is not an array!");
        }
        ArrayNode filtersArray = (ArrayNode) filtersJson;
        for (int i = 0; i < filtersArray.size(); i++) {
            validateActionJson(filtersArray.get(i), ComponentType.ACTION);
        }
    }

    private void validateDataSourceOrFilterJson(JsonNode json, ComponentType componentType) {
        if (json == null || json.isNull()) {
            throw new IncorrectParameterException(componentType.name() + " is required!");
        }
        String type = getIfValid(componentType.name(), json, "type", JsonNode::isTextual, JsonNode::asText);
        JsonNode configuration = getIfValid(componentType.name(), json, "configuration", JsonNode::isObject, node -> node);

        switch (componentType) {    // 验证dataSource 节点下的 type、topic,format,keys

            case RULE:{
                if (!configuration.isNull()){
                    //JsonOb dataSourceNode = configuration
                    if ("kafka".equals(type)){
                        String topic = getIfValid(componentType.name(), configuration, "topic", JsonNode::isTextual, JsonNode::asText);
                        String format = getIfValid(componentType.name(), configuration, "format", JsonNode::isTextual, JsonNode::asText);
                        JsonNode keys = getIfValid(componentType.name(), configuration, "keys", JsonNode::isArray, node -> node);
                    }else if ("spark".equals(type)) {
                        // TODO: 2018/1/18 0018   处理spark相关配置
                    }
                }
                log.info("RuleDataSource = {} {}", type,configuration.toString());
            }
            break;
            case FILTER:{    // 验证filters 节点下的name、type、condition
                String name = getIfValid(componentType.name(), json, "name", JsonNode::isTextual, JsonNode::asText);
                String condition = getIfValid(componentType.name(), json, "condition", JsonNode::isTextual, JsonNode::asText);
                if (!configuration.isNull()){
                    if ("single".equals(type)){
                        // TODO: 2018/1/18 0018
                    }else if ("window".equals(type)) {
                        // TODO: 2018/1/18 0018
                    }
                }
                log.info("Filter = {},{},{}", name, type, condition);
            }
            break;
        }
    }

    // 验证action节点下的type、address,level,template
    private void validateActionJson(JsonNode json, ComponentType componentType) {
        if (json == null || json.isNull()) {
            throw new IncorrectParameterException(componentType.name() + " is required!");
        }
        String type = getIfValid(componentType.name(), json, "type", JsonNode::isTextual, JsonNode::asText);
        //String address = getIfValid(componentType.name(), json, "address", JsonNode::isTextual, JsonNode::asText);
        JsonNode address = getIfValid(componentType.name(), json, "address", JsonNode::isArray, node -> node);

        String level = getIfValid(componentType.name(), json, "level", JsonNode::isTextual, JsonNode::asText);
        String template = getIfValid(componentType.name(), json, "template", JsonNode::isTextual, JsonNode::asText);

        log.info("actionsType = {},{},{}", type, address.toString(), level, template);
    }

    private static <T> T getIfValid(String parentName, JsonNode node, String name, Function<JsonNode, Boolean> validator, Function<JsonNode, T> extractor) {
        if (!node.has(name)) {
            throw new IncorrectParameterException(parentName + "'s " + name + " is not set!");
        } else {
            JsonNode value = node.get(name);
            if (validator.apply(value)) {
                return extractor.apply(value);
            } else {
                throw new IncorrectParameterException(parentName + "'s " + name + " is not valid!");
            }
        }
    }

    @Override
    public RuleMetaData findRuleById(RuleId ruleId) {
        validateId(ruleId, "Incorrect Rule id for search Rule request.");
        return ruleDao.findById(ruleId.getId());
    }

    @Override
    public ListenableFuture<RuleMetaData> findRuleByIdAsync(RuleId ruleId) {
        validateId(ruleId, "Incorrect Rule id for search Rule request.");
        return ruleDao.findByIdAsync(ruleId.getId());
    }

    @Override
    public List<RuleMetaData> findPluginRules(String pluginToken) {
        return ruleDao.findRulesByPlugin(pluginToken);
    }

    @Override
    public TextPageData<RuleMetaData> findSystemRules(TextPageLink pageLink) {
        validatePageLink(pageLink, "Incorrect PageLink object for search Rule request.");
        //systemTenantId ===
        List<RuleMetaData> rules = ruleDao.findSystemRules();//ruleDao.findByTenantIdAndPageLink(systemTenantId, pageLink);
        return new TextPageData<>(rules, pageLink);
    }

    @Override
    public TextPageData<RuleMetaData> findTenantRules(UserId tenantId, TextPageLink pageLink) {
        return null;
    }

    @Override
    public List<RuleMetaData> findSystemRules() {
        log.trace("Executing findSystemRules");
        List<RuleMetaData> rules = new ArrayList<>();
        TextPageLink pageLink = new TextPageLink(300);
        TextPageData<RuleMetaData> pageData = null;
        do {
            pageData = findSystemRules(pageLink);
            rules.addAll(pageData.getData());
            if (pageData.hasNext()) {
                pageLink = pageData.getNextPageLink();
            }
        } while (pageData.hasNext());
        return rules;
    }

    @Override
    public void deleteRuleById(RuleId ruleId) {
        validateId(ruleId, "Incorrect Rule id for delete Rule request.");
        deleteEntityRelations(ruleId);
        ruleDao.deleteById(ruleId);
    }

    @Override
    public List<RuleMetaData> findAllUserRulesByUserId(UserId tenantId) {
        return null;
    }

    @Override
    public void activateRuleById(RuleId ruleId) {
        updateLifeCycleState(ruleId, ComponentLifecycleState.ACTIVE);
    }

    @Override
    public void suspendRuleById(RuleId ruleId) {
        updateLifeCycleState(ruleId, ComponentLifecycleState.SUSPENDED);
    }

    private void updateLifeCycleState(RuleId ruleId, ComponentLifecycleState state) {
        Validator.validateId(ruleId, "Incorrect Rule id for state change request.");
        RuleMetaData rule = ruleDao.findById(ruleId);
        if (rule != null) {
            rule.setState(state);
            //validateRuleAndPluginState(Rule);
            ruleDao.save(rule);
        } else {
            throw new DatabaseException("Plugin not found!");
        }
    }
}
