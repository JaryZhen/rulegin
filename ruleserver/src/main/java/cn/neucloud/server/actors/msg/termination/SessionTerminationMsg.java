package cn.neucloud.server.actors.msg.termination;


import cn.neucloud.server.common.data.id.SessionId;

public class SessionTerminationMsg extends ActorTerminationMsg<SessionId> {

    public SessionTerminationMsg(SessionId id) {
        super(id);
    }
}
