package tb.rulegin.server.core.rule;


import tb.rulegin.server.common.component.ConfigurableComponent;


public interface RuleFilter<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    boolean filter(RuleContext ctx);
    
}
