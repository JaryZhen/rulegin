
package com.github.rulegin.actors.msg;


import java.io.Serializable;
import java.util.Optional;


public class BasicResponseMsg<T extends Serializable> implements ResponseMsg<T> {

    private static final long serialVersionUID = 1L;

    private final MsgType requestMsgType;
    private final Integer requestId;
    private final MsgType msgType;
    private final boolean success;
    private final T data;
    private final Exception error;

    protected BasicResponseMsg(MsgType requestMsgType, Integer requestId, MsgType msgType, boolean success, Exception error, T data) {
        super();
        this.requestMsgType = requestMsgType;
        this.requestId = requestId;
        this.msgType = msgType;
        this.success = success;
        this.error = error;
        this.data = data;
    }

    @Override
    public MsgType getRequestMsgType() {
        return requestMsgType;
    }

    @Override
    public Integer getRequestId() {
        return requestId;
    }


    @Override
    public Optional<Exception> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    @Override
    public String toString() {
        return "BasicResponseMsg [success=" + success + ", dataSource=" + data + ", error=" + error + "]";
    }

}
