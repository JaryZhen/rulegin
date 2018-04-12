
package tb.rulegin.server.core.action.template;

import tb.rulegin.server.core.action.plugins.PluginAction;
import tb.rulegin.server.core.rule.SimpleRuleLifecycleComponent;
import tb.rulegin.server.utils.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.ParseException;


@Slf4j
public abstract class AbstractTemplatePluginAction<T extends TemplateActionConfiguration> extends SimpleRuleLifecycleComponent implements PluginAction<T> {
    protected T configuration;
    protected Template template;

    @Override
    public void run(T configuration) {
        this.configuration = configuration;
        try {
            this.template = VelocityUtils.create(configuration.getTemplate(), "Template");
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public boolean isOneWayAction() {
        return !configuration.isSync();
    }
}
