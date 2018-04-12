package tb.rulegin.server.actors.msg.core;

import tb.rulegin.server.actors.service.cluster.ServerAddress;
import lombok.Data;
import tb.rulegin.server.gen.cluster.ClusterAPIProtos;


@Data
public final class RpcSessionTellMsg {
    private final ServerAddress serverAddress;
    private final ClusterAPIProtos.ToRpcServerMessage msg;
}
