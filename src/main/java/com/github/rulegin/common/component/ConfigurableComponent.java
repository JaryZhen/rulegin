package com.github.rulegin.common.component;

public interface ConfigurableComponent<T> {

    void run(T configuration);

}
