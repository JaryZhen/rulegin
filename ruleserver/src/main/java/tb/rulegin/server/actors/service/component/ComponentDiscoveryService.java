package tb.rulegin.server.actors.service.component;


import tb.rulegin.server.common.data.component.ComponentType;
import tb.rulegin.server.common.data.component.DefineComponent;

import java.util.List;
import java.util.Optional;

public interface ComponentDiscoveryService {

    void discoverComponents();

    List<DefineComponent> getComponents(ComponentType type);

    Optional<DefineComponent> getComponent(String clazz);

    Optional<DefineComponent> getDefineComponent(String clazz);

    List<DefineComponent> getPluginActions(String pluginClazz);

}
