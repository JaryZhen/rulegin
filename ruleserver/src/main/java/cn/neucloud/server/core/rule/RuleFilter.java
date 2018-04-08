package cn.neucloud.server.core.rule;


import cn.neucloud.server.common.component.ConfigurableComponent;


public interface RuleFilter<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    boolean filter(RuleContext ctx);
    
}
