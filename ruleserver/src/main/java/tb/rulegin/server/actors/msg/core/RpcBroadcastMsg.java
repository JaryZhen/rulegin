
package tb.rulegin.server.actors.msg.core;

import lombok.Data;
import tb.rulegin.server.gen.cluster.ClusterAPIProtos;


@Data
public final class RpcBroadcastMsg {
    private final ClusterAPIProtos.ToRpcServerMessage msg;
}
