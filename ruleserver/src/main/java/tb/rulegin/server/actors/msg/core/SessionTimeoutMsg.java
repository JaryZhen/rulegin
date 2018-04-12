package tb.rulegin.server.actors.msg.core;

import tb.rulegin.server.common.data.id.SessionId;
import lombok.Data;

import java.io.Serializable;

@Data
public class SessionTimeoutMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SessionId sessionId;
}
