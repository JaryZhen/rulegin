package com.github.rulegin.actors.msg.cluster;

import com.github.rulegin.common.data.component.ComponentLifecycleEvent;
import com.github.rulegin.common.data.id.PluginId;
import com.github.rulegin.common.data.id.RuleId;
import com.github.rulegin.common.data.id.UserId;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@ToString
public class ComponentLifecycleMsg implements  ToAllNodesMsg {

    private final PluginId pluginId;
    private final RuleId ruleId;
    @Getter
    private final UserId userId;
    @Getter
    private final ComponentLifecycleEvent event;

    public static ComponentLifecycleMsg forPlugin( PluginId pluginId, UserId userId,ComponentLifecycleEvent event) {
        return new ComponentLifecycleMsg (pluginId, null,  userId,event);
    }

    public static ComponentLifecycleMsg forRule( RuleId ruleId, UserId userId,ComponentLifecycleEvent event) {
        return new ComponentLifecycleMsg( null, ruleId,userId, event);
    }

    private ComponentLifecycleMsg( PluginId pluginId, RuleId ruleId,UserId userId, ComponentLifecycleEvent event) {
        this.pluginId = pluginId;
        this.ruleId = ruleId;
        this.event = event;
        this.userId = userId;
    }

    public Optional<PluginId> getPluginId() {
        return Optional.ofNullable(pluginId);
    }

    public Optional<RuleId> getRuleId() {
        return Optional.ofNullable(ruleId);
    }
}
