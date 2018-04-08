package cn.neucloud.server.dao.component;

import cn.neucloud.server.common.data.component.ComponentDescriptor;
import cn.neucloud.server.common.data.component.ComponentScope;
import cn.neucloud.server.common.data.component.ComponentType;
import cn.neucloud.server.common.data.id.ComponentDescriptorId;
import cn.neucloud.server.common.data.page.TextPageData;
import cn.neucloud.server.common.data.page.TextPageLink;
import cn.neucloud.server.dao.exception.DataValidationException;
import cn.neucloud.server.dao.exception.IncorrectParameterException;
import cn.neucloud.server.dao.util.DataValidator;
import cn.neucloud.server.dao.util.Validator;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 */
@Service
@Slf4j
public class BaseComponentDescriptorService implements ComponentDescriptorService {

    @Autowired
    private ComponentDescriptorDao componentDescriptorDao;

    @Override
    public ComponentDescriptor saveComponent(ComponentDescriptor component) {
        componentValidator.validate(component);
        Optional<ComponentDescriptor> result = componentDescriptorDao.saveIfNotExist(component);
        if (result.isPresent()) {
            return result.get();
        } else {
            return componentDescriptorDao.findByClazz(component.getClazz());
        }
    }

    @Override
    public ComponentDescriptor findById(ComponentDescriptorId componentId) {
        Validator.validateId(componentId, "Incorrect component id for search request.");
        return componentDescriptorDao.findById(componentId);
    }

    @Override
    public ComponentDescriptor findByClazz(String clazz) {
        Validator.validateString(clazz, "Incorrect clazz for search request.");
        return componentDescriptorDao.findByClazz(clazz);
    }

    @Override
    public TextPageData<ComponentDescriptor> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink) {
        Validator.validatePageLink(pageLink, "Incorrect PageLink object for search plugin components request.");
        List<ComponentDescriptor> components = componentDescriptorDao.findByTypeAndPageLink(type, pageLink);
        return new TextPageData<>(components, pageLink);
    }

    @Override
    public TextPageData<ComponentDescriptor> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink) {
        Validator.validatePageLink(pageLink, "Incorrect PageLink object for search plugin components request.");
        List<ComponentDescriptor> components = componentDescriptorDao.findByScopeAndTypeAndPageLink(scope, type, pageLink);
        return new TextPageData<>(components, pageLink);
    }

    @Override
    public void deleteByClazz(String clazz) {
        Validator.validateString(clazz, "Incorrect clazz for delete request.");
        componentDescriptorDao.deleteByClazz(clazz);
    }

    @Override
    public boolean validate(ComponentDescriptor component, JsonNode configuration) {
        JsonValidator validator = JsonSchemaFactory.byDefault().getValidator();
        try {
            if (!component.getConfigurationDescriptor().has("schema")) {
                throw new DataValidationException("Configuration descriptor doesn't contain schema property!");
            }
            JsonNode configurationSchema = component.getConfigurationDescriptor().get("schema");
            ProcessingReport report = validator.validate(configurationSchema, configuration);
            return report.isSuccess();
        } catch (ProcessingException e) {
            throw new IncorrectParameterException(e.getMessage(), e);
        }
    }

    private DataValidator<ComponentDescriptor> componentValidator =
            new DataValidator<ComponentDescriptor>() {
                @Override
                protected void validateDataImpl(ComponentDescriptor plugin) {
                    if (plugin.getType() == null) {
                        throw new DataValidationException("Component type should be specified!.");
                    }
                    if (plugin.getScope() == null) {
                        throw new DataValidationException("Component scope should be specified!.");
                    }
                    if (StringUtils.isEmpty(plugin.getName())) {
                        throw new DataValidationException("Component name should be specified!.");
                    }
                    if (StringUtils.isEmpty(plugin.getClazz())) {
                        throw new DataValidationException("Component clazz should be specified!.");
                    }
                }
            };
}
