package top.liuyisong.rpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.liuyisong.rpc.entity.RpcRequest;
import top.liuyisong.rpc.entity.RpcResponse;
import top.liuyisong.rpc.enumeration.ResponseCode;
import top.liuyisong.rpc.provider.ServiceProvider;
import top.liuyisong.rpc.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 * 用来实现代理
 * 算是比较上层了，持有provider
 * 而provider只是一个map的封装
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    //持有服务注册的对象
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest rpcRequest) {
        //从本地拿到服务
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        //执行该服务，反射执行拿到结果
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            //反射执行
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return result;
    }

}
