package cn.neucloud.server.core.rule;


import cn.neucloud.server.common.component.ConfigurableComponent;


public interface RuleProcessor<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    RuleProcessingMetaData process(RuleContext ctx) throws RuleException;
}
