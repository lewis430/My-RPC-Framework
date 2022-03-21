package top.liuyisong.rpc.transport;

import top.liuyisong.rpc.entity.RpcRequest;
import top.liuyisong.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
