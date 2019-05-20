
package com.github.rulegin.actors.msg.core;

import com.github.rulegin.actors.service.cluster.ServerAddress;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 */
@ToString
@RequiredArgsConstructor
public class RpcMsg {
    @Getter
    private final ServerAddress serverAddress;
    @Getter
    private final int msgClazz;
    @Getter
    private final byte[] msgData;
}
