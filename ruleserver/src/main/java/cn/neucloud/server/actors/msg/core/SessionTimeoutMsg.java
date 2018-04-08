package cn.neucloud.server.actors.msg.core;

import cn.neucloud.server.common.data.id.SessionId;
import lombok.Data;

import java.io.Serializable;

@Data
public class SessionTimeoutMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SessionId sessionId;
}
