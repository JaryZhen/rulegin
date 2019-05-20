package com.github.rulegin.actors.service.component;


import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.component.DefineComponent;

import java.util.List;
import java.util.Optional;

public interface ComponentDiscoveryService {

    void discoverComponents();

    List<DefineComponent> getComponents(ComponentType type);

    Optional<DefineComponent> getComponent(String clazz);

    Optional<DefineComponent> getDefineComponent(String clazz);

    List<DefineComponent> getPluginActions(String pluginClazz);

}
