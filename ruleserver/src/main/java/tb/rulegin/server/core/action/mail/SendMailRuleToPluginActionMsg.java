package tb.rulegin.server.core.action.mail;

import tb.rulegin.server.common.data.id.DeviceId;
import tb.rulegin.server.common.data.id.UserId;
import tb.rulegin.server.actors.msg.rule.AbstractRuleToPluginMsg;
import lombok.Data;

@Data
public class SendMailRuleToPluginActionMsg extends AbstractRuleToPluginMsg<SendMailActionMsg> {

    public SendMailRuleToPluginActionMsg(UserId tenantId, DeviceId deviceId,
                                         SendMailActionMsg payload) {
        super(tenantId, deviceId, payload);
    }

}
