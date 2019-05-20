package com.github.rulegin.core.action.mail;

import com.github.rulegin.actors.msg.rule.RuleToPluginMsg;
import com.github.rulegin.common.component.Action;
import com.github.rulegin.core.action.plugins.PluginAction;
import com.github.rulegin.core.rule.RuleContext;
import com.github.rulegin.core.rule.RuleProcessingMetaData;
import com.github.rulegin.core.rule.SimpleRuleLifecycleComponent;
import com.github.rulegin.utils.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.ParseException;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Action(name = "Send Mail Action", descriptor = "SendMailActionDescriptor.json", configuration = SendMailActionConfiguration.class)
@Slf4j
public class SendMailAction extends SimpleRuleLifecycleComponent implements PluginAction<SendMailActionConfiguration> {

    private SendMailActionConfiguration configuration;
    private Optional<Template> fromTemplate;
    private Optional<Template> toTemplate;
    private Optional<Template> ccTemplate;
    private Optional<Template> bccTemplate;
    private Optional<Template> subjectTemplate;
    private Optional<Template> bodyTemplate;

    @Override
    public void run(SendMailActionConfiguration configuration) {
        this.configuration = configuration;
        try {
            fromTemplate = toTemplate(configuration.getFromTemplate(), "From Template");
            toTemplate = toTemplate(configuration.getToTemplate(), "To Template");
            ccTemplate = toTemplate(configuration.getCcTemplate(), "Cc Template");
            bccTemplate = toTemplate(configuration.getBccTemplate(), "Bcc Template");
            subjectTemplate = toTemplate(configuration.getSubjectTemplate(), "Subject Template");
            bodyTemplate = toTemplate(configuration.getBodyTemplate(), "Body Template");
        } catch (ParseException e) {
            log.error("Failed to create templates based on provided configuration!", e);
            throw new RuntimeException("Failed to create templates based on provided configuration!", e);
        }
    }

    private Optional<Template> toTemplate(String source, String name) throws ParseException {
        if (!StringUtils.isEmpty(source)) {
            return Optional.of(VelocityUtils.create(source, name));
        } else {
            return Optional.empty();
        }
    }


    @Override
    public Optional<RuleToPluginMsg<?>> convert(RuleContext ctx, RuleProcessingMetaData deviceMsgMd) {
        return null;
    }

    @Override
    public boolean isOneWayAction() {
        return true;
    }

}
