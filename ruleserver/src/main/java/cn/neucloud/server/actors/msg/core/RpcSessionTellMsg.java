package cn.neucloud.server.actors.msg.core;

import cn.neucloud.server.actors.service.cluster.ServerAddress;
import lombok.Data;
import org.neurule.server.gen.cluster.ClusterAPIProtos;


@Data
public final class RpcSessionTellMsg {
    private final ServerAddress serverAddress;
    private final ClusterAPIProtos.ToRpcServerMessage msg;
}
