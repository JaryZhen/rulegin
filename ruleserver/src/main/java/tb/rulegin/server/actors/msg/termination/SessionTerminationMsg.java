package tb.rulegin.server.actors.msg.termination;


import tb.rulegin.server.common.data.id.SessionId;

public class SessionTerminationMsg extends ActorTerminationMsg<SessionId> {

    public SessionTerminationMsg(SessionId id) {
        super(id);
    }
}
