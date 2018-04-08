package cn.neucloud.server.core.rule;


import cn.neucloud.server.common.component.ConfigurableComponent;


public interface RuleDataSource<T> extends ConfigurableComponent<T>, RuleLifecycleComponent {

    boolean some();
    
}
