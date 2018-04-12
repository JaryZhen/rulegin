package tb.rulegin.server.core.rule;


import tb.rulegin.server.common.component.ConfigurableComponent;


public interface RuleDataSource<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    boolean some();
    
}
