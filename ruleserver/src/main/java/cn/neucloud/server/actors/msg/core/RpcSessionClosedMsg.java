package cn.neucloud.server.actors.msg.core;

import cn.neucloud.server.actors.service.cluster.ServerAddress;
import lombok.Data;

@Data
public final class RpcSessionClosedMsg {

    private final boolean client;
    private final ServerAddress remoteAddress;
}
