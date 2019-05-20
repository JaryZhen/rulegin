
package com.github.rulegin.actors.msg.core;

import com.github.rulegin.server.gen.cluster.ClusterAPIProtos;
import lombok.Data;


@Data
public final class RpcBroadcastMsg {
    private final ClusterAPIProtos.ToRpcServerMessage msg;
}
