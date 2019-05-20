package com.github.rulegin.actors.msg.core;

import com.github.rulegin.common.data.id.SessionId;
import lombok.Data;

import java.io.Serializable;

@Data
public class SessionTimeoutMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SessionId sessionId;
}
