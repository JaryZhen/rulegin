package com.github.rulegin.actors.msg.core;

import com.github.rulegin.actors.service.cluster.ServerAddress;
import com.github.rulegin.server.gen.cluster.ClusterAPIProtos;
import lombok.Data;


@Data
public final class RpcSessionTellMsg {
    private final ServerAddress serverAddress;
    private final ClusterAPIProtos.ToRpcServerMessage msg;
}
