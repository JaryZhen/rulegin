
package tb.rulegin.server.actors.msg.core;

import tb.rulegin.server.actors.service.cluster.ServerAddress;
import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public final class RpcSessionDisconnectedMsg {

    private final boolean client;
    private final ServerAddress remoteAddress;
}
