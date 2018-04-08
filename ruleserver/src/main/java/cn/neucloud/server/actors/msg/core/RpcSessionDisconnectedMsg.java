
package cn.neucloud.server.actors.msg.core;

import cn.neucloud.server.actors.service.cluster.ServerAddress;
import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public final class RpcSessionDisconnectedMsg {

    private final boolean client;
    private final ServerAddress remoteAddress;
}
