
package cn.neucloud.server.actors.msg.plugin.torule;


import cn.neucloud.server.common.data.id.RuleId;
import cn.neucloud.server.common.data.id.UserId;

import java.io.Serializable;
import java.util.UUID;

public class AbstractPluginToRuleMsg<T extends Serializable> implements PluginToRuleMsg<T> {

    private static final long serialVersionUID = 1L;

    private final UUID uid;
    private final RuleId ruleId;
    private final T payload;
    private final UserId userId;
    public AbstractPluginToRuleMsg(UUID uid,  RuleId ruleId,UserId userId, T payload) {
        super();
        this.uid = uid;
        this.ruleId = ruleId;
        this.payload = payload;
        this.userId = userId;
    }
    public AbstractPluginToRuleMsg(UUID uid,  RuleId ruleId, T payload) {
        this(uid,ruleId,null,payload);
    }
    @Override
    public UUID getUid() {
        return uid;
    }

    @Override
    public UserId getUserId() {
        return userId;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public RuleId getRuleId() {
        return ruleId;
    }



}
