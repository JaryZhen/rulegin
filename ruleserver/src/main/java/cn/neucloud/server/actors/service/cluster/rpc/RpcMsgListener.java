
package cn.neucloud.server.actors.service.cluster.rpc;


import cn.neucloud.server.actors.msg.cluster.ToAllNodesMsg;
import cn.neucloud.server.actors.msg.plugin.aware.ToPluginActorMsg;
import cn.neucloud.server.actors.msg.core.RpcBroadcastMsg;
import cn.neucloud.server.actors.msg.core.RpcSessionCreateRequestMsg;
import cn.neucloud.server.actors.msg.core.TimeoutMsg;

public interface RpcMsgListener {

    void onMsg(ToAllNodesMsg nodeMsg);

    void onMsg(ToPluginActorMsg msg);

    void onMsg(RpcSessionCreateRequestMsg msg);

    void onMsg(TimeoutMsg.RpcSessionTellMsg rpcSessionTellMsg);

    void onMsg(RpcBroadcastMsg rpcBroadcastMsg);

}
