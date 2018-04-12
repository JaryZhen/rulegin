package tb.rulegin.server.actors.rpc;

import akka.actor.ActorRef;
import lombok.Data;

import java.util.UUID;

@Data
public final class SessionActorInfo {
    protected final UUID sessionId;
    protected final ActorRef actor;
}
