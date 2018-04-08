package cn.neucloud.server.core.action.mail;

import cn.neucloud.server.common.data.id.DeviceId;
import cn.neucloud.server.common.data.id.UserId;
import cn.neucloud.server.actors.msg.rule.AbstractRuleToPluginMsg;
import lombok.Data;

@Data
public class SendMailRuleToPluginActionMsg extends AbstractRuleToPluginMsg<SendMailActionMsg> {

    public SendMailRuleToPluginActionMsg(UserId tenantId, DeviceId deviceId,
                                         SendMailActionMsg payload) {
        super(tenantId, deviceId, payload);
    }

}
