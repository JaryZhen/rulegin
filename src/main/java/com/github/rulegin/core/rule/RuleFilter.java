package com.github.rulegin.core.rule;


import com.github.rulegin.common.component.ConfigurableComponent;


public interface RuleFilter<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    boolean filter(RuleContext ctx);
    
}
