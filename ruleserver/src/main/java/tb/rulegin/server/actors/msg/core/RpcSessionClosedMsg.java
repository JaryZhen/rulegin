package tb.rulegin.server.actors.msg.core;

import tb.rulegin.server.actors.service.cluster.ServerAddress;
import lombok.Data;

@Data
public final class RpcSessionClosedMsg {

    private final boolean client;
    private final ServerAddress remoteAddress;
}
