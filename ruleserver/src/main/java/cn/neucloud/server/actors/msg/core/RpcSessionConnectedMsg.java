package cn.neucloud.server.actors.msg.core;

import cn.neucloud.server.actors.service.cluster.ServerAddress;
import lombok.Data;

import java.util.UUID;


@Data
public final class RpcSessionConnectedMsg {

    private final ServerAddress remoteAddress;
    private final UUID id;
}
