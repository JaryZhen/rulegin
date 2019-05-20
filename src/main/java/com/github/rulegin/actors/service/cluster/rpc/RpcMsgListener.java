
package com.github.rulegin.actors.service.cluster.rpc;


import com.github.rulegin.actors.msg.cluster.ToAllNodesMsg;
import com.github.rulegin.actors.msg.core.RpcBroadcastMsg;
import com.github.rulegin.actors.msg.core.RpcSessionCreateRequestMsg;
import com.github.rulegin.actors.msg.core.TimeoutMsg;
import com.github.rulegin.actors.msg.plugin.aware.ToPluginActorMsg;

public interface RpcMsgListener {

    void onMsg(ToAllNodesMsg nodeMsg);

    void onMsg(ToPluginActorMsg msg);

    void onMsg(RpcSessionCreateRequestMsg msg);

    void onMsg(TimeoutMsg.RpcSessionTellMsg rpcSessionTellMsg);

    void onMsg(RpcBroadcastMsg rpcBroadcastMsg);

}
