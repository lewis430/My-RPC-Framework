package top.liuyisong.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.liuyisong.rpc.entity.RpcRequest;
import top.liuyisong.rpc.entity.RpcResponse;
import top.liuyisong.rpc.enumeration.ResponseCode;
import top.liuyisong.rpc.enumeration.RpcError;
import top.liuyisong.rpc.exception.RpcException;

/**
 * 检查响应与请求
 */
public class RpcMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker() {
    }

    //用来抛异常
    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        if (rpcResponse == null) {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        //响应和请求号不匹配：请求ID和响应ID对不上
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        //服务调用出现失败：响应的状态码为null或者不成功
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }

}
