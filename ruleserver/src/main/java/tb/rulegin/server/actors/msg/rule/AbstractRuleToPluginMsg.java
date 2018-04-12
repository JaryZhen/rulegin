
package tb.rulegin.server.actors.msg.rule;


import tb.rulegin.server.common.data.id.DeviceId;
import tb.rulegin.server.common.data.id.UserId;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractRuleToPluginMsg<T extends Serializable> implements RuleToPluginMsg<T> {

    private static final long serialVersionUID = 1L;

    private final UUID uid;
    private final UserId customerId;
    private final DeviceId deviceId;
    private final T payload;

    public AbstractRuleToPluginMsg(UserId customerId, DeviceId deviceId, T payload) {
        super();
        this.uid = UUID.randomUUID();
        this.customerId = customerId;
        this.deviceId = deviceId;
        this.payload = payload;
    }

    @Override
    public UUID getUid() {
        return uid;
    }

    @Override
    public DeviceId getDeviceId() {
        return deviceId;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "AbstractRuleToPluginMsg [uid=" + uid  + ", customerId=" + customerId
                + ", deviceId=" + deviceId + ", payload=" + payload + "]";
    }

    @Override
    public UserId getUserId() {
        return customerId;
    }
}
