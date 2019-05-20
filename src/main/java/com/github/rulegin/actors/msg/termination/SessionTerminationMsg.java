package com.github.rulegin.actors.msg.termination;


import com.github.rulegin.common.data.id.SessionId;

public class SessionTerminationMsg extends ActorTerminationMsg<SessionId> {

    public SessionTerminationMsg(SessionId id) {
        super(id);
    }
}
