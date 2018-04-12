package tb.rulegin.server.actors.msg.core;

import tb.rulegin.server.actors.service.cluster.ServerAddress;
import lombok.Data;

import java.util.UUID;


@Data
public final class RpcSessionConnectedMsg {

    private final ServerAddress remoteAddress;
    private final UUID id;
}
