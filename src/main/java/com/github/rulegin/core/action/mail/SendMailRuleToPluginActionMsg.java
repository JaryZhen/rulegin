package com.github.rulegin.core.action.mail;

import com.github.rulegin.actors.msg.rule.AbstractRuleToPluginMsg;
import com.github.rulegin.common.data.id.DeviceId;
import com.github.rulegin.common.data.id.UserId;
import lombok.Data;

@Data
public class SendMailRuleToPluginActionMsg extends AbstractRuleToPluginMsg<SendMailActionMsg> {

    public SendMailRuleToPluginActionMsg(UserId tenantId, DeviceId deviceId,
                                         SendMailActionMsg payload) {
        super(tenantId, deviceId, payload);
    }

}
