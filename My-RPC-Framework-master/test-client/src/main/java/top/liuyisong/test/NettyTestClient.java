package top.liuyisong.test;

import top.liuyisong.rpc.api.ByeService;
import top.liuyisong.rpc.api.HelloObject;
import top.liuyisong.rpc.api.HelloService;
import top.liuyisong.rpc.serializer.CommonSerializer;
import top.liuyisong.rpc.transport.RpcClient;
import top.liuyisong.rpc.transport.RpcClientProxy;
import top.liuyisong.rpc.transport.netty.client.NettyClient;

/**
 * 测试用Netty消费者
 */
public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }

}
