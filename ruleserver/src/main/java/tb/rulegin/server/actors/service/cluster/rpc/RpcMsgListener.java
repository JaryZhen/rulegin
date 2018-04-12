
package tb.rulegin.server.actors.service.cluster.rpc;


import tb.rulegin.server.actors.msg.cluster.ToAllNodesMsg;
import tb.rulegin.server.actors.msg.plugin.aware.ToPluginActorMsg;
import tb.rulegin.server.actors.msg.core.RpcBroadcastMsg;
import tb.rulegin.server.actors.msg.core.RpcSessionCreateRequestMsg;
import tb.rulegin.server.actors.msg.core.TimeoutMsg;

public interface RpcMsgListener {

    void onMsg(ToAllNodesMsg nodeMsg);

    void onMsg(ToPluginActorMsg msg);

    void onMsg(RpcSessionCreateRequestMsg msg);

    void onMsg(TimeoutMsg.RpcSessionTellMsg rpcSessionTellMsg);

    void onMsg(RpcBroadcastMsg rpcBroadcastMsg);

}
