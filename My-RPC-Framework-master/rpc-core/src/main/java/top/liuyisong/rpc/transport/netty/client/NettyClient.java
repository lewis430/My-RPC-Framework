package top.liuyisong.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.liuyisong.rpc.entity.RpcRequest;
import top.liuyisong.rpc.entity.RpcResponse;
import top.liuyisong.rpc.enumeration.RpcError;
import top.liuyisong.rpc.exception.RpcException;
import top.liuyisong.rpc.factory.SingletonFactory;
import top.liuyisong.rpc.loadbalancer.LoadBalancer;
import top.liuyisong.rpc.loadbalancer.RandomLoadBalancer;
import top.liuyisong.rpc.registry.NacosServiceDiscovery;
import top.liuyisong.rpc.registry.ServiceDiscovery;
import top.liuyisong.rpc.serializer.CommonSerializer;
import top.liuyisong.rpc.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费侧客户端类
 * 用到的组件：
 *      ServiceDiscovery：用于服务的注册
 *      CommonSerializer：序列化器
 *      RandomLoadBalancer：负载均衡策略
 *  客户端主要封装好request，指定序列化器，负载均衡策略，用服务发现来连接netty
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    //服务发现
    private final ServiceDiscovery serviceDiscovery;
    //序列化器
    private final CommonSerializer serializer;
    //未处理的请求
    private final UnprocessedRequests unprocessedRequests;

    //默认
    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    //有一个参数的构造函数
    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }
    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //监听
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //先找nacos要ip再用netty连接
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //把连接netty抽象出来了
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            //放到未处理的map中排队
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
