package com.github.rulegin.actors.msg.core;

import com.github.rulegin.actors.service.cluster.ServerAddress;
import lombok.Data;

@Data
public final class RpcSessionClosedMsg {

    private final boolean client;
    private final ServerAddress remoteAddress;
}
