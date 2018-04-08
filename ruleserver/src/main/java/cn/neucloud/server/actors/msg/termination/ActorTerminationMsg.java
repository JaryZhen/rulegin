
package cn.neucloud.server.actors.msg.termination;

public abstract class ActorTerminationMsg<T> {

    private final T id;

    public ActorTerminationMsg(T id) {
        super();
        this.id = id;
    }

    public T getId() {
        return id;
    }

}
