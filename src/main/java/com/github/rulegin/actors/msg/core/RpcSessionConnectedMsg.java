package com.github.rulegin.actors.msg.core;

import com.github.rulegin.actors.service.cluster.ServerAddress;
import lombok.Data;

import java.util.UUID;


@Data
public final class RpcSessionConnectedMsg {

    private final ServerAddress remoteAddress;
    private final UUID id;
}
