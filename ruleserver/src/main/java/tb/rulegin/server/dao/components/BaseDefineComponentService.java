package tb.rulegin.server.dao.components;

import tb.rulegin.server.common.data.component.DefineComponent;
import tb.rulegin.server.common.data.component.ComponentScope;
import tb.rulegin.server.common.data.component.ComponentType;
import tb.rulegin.server.common.data.id.ComponentDescriptorId;
import tb.rulegin.server.common.data.page.TextPageData;
import tb.rulegin.server.common.data.page.TextPageLink;
import tb.rulegin.server.dao.exception.DataValidationException;
import tb.rulegin.server.dao.exception.IncorrectParameterException;
import tb.rulegin.server.dao.util.DataValidator;
import tb.rulegin.server.dao.util.Validator;
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
public class BaseDefineComponentService implements DefineComponentService {

    @Autowired
    private DefineComponentDao descriptorDao;

    @Override
    public DefineComponent saveComponent(DefineComponent component) {
        componentValidator.validate(component);
        Optional<DefineComponent> result = descriptorDao.saveIfNotExist(component);
        if (result.isPresent()) {
            return result.get();
        } else {
            return descriptorDao.findByClazz(component.getClazz());
        }
    }

    @Override
    public DefineComponent findById(ComponentDescriptorId componentId) {
        Validator.validateId(componentId, "Incorrect component id for search request.");
        return descriptorDao.findById(componentId);
    }

    @Override
    public DefineComponent findByClazz(String clazz) {
        Validator.validateString(clazz, "Incorrect clazz for search request.");
        return descriptorDao.findByClazz(clazz);
    }

    public DefineComponent findByDataSourceTypeAndFiltersType(String data, String filter) {
        return descriptorDao.findByDataSourceTypeAndFiltersType(data,filter);
    }

    @Override
    public TextPageData<DefineComponent> findByTypeAndPageLink(ComponentType type, TextPageLink pageLink) {
        Validator.validatePageLink(pageLink, "Incorrect PageLink object for search plugin components request.");
        List<DefineComponent> components = descriptorDao.findByTypeAndPageLink(type, pageLink);
        return new TextPageData<>(components, pageLink);
    }

    @Override
    public TextPageData<DefineComponent> findByScopeAndTypeAndPageLink(ComponentScope scope, ComponentType type, TextPageLink pageLink) {
        Validator.validatePageLink(pageLink, "Incorrect PageLink object for search plugin components request.");
        List<DefineComponent> components = descriptorDao.findByScopeAndTypeAndPageLink(scope, type, pageLink);
        return new TextPageData<>(components, pageLink);
    }

    @Override
    public void deleteByClazz(String clazz) {
        Validator.validateString(clazz, "Incorrect clazz for delete request.");
        descriptorDao.deleteByClazz(clazz);
    }

    @Override
    public boolean validate(DefineComponent component, JsonNode configuration) {
        JsonValidator validator = JsonSchemaFactory.byDefault().getValidator();
        try {

            JsonNode configurationSchema = null;//component.getConfigurationDescriptor().get("schema");
            ProcessingReport report = validator.validate(configurationSchema, configuration);
            return report.isSuccess();
        } catch (ProcessingException e) {
            throw new IncorrectParameterException(e.getMessage(), e);
        }
    }

    private DataValidator<DefineComponent> componentValidator =
            new DataValidator<DefineComponent>() {
                @Override
                protected void validateDataImpl(DefineComponent plugin) {
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
