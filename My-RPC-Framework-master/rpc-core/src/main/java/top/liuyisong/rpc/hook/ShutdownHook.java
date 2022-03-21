package top.liuyisong.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.liuyisong.rpc.factory.ThreadPoolFactory;
import top.liuyisong.rpc.util.NacosUtil;

/**
 * 主要是处理RunTime对象，里面加上钩子
 * 使用时放在nettyServer中执行
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
