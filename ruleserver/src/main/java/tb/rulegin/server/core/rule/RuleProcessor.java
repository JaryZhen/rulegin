package tb.rulegin.server.core.rule;


import tb.rulegin.server.common.component.ConfigurableComponent;


public interface RuleProcessor<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    RuleProcessingMetaData process(RuleContext ctx) throws RuleException;
}
