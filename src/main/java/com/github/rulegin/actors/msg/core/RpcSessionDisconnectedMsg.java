
package com.github.rulegin.actors.msg.core;

import com.github.rulegin.actors.service.cluster.ServerAddress;
import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public final class RpcSessionDisconnectedMsg {

    private final boolean client;
    private final ServerAddress remoteAddress;
}
