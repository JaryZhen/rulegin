
package cn.neucloud.server.actors.msg.core;

import lombok.Data;
import org.neurule.server.gen.cluster.ClusterAPIProtos;


@Data
public final class RpcBroadcastMsg {
    private final ClusterAPIProtos.ToRpcServerMessage msg;
}
