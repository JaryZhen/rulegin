package com.github.rulegin.core.rule;


import com.github.rulegin.common.component.ConfigurableComponent;


public interface RuleProcessor<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    RuleProcessingMetaData process(RuleContext ctx) throws RuleException;
}
