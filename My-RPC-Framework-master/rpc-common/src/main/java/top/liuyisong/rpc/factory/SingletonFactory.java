package top.liuyisong.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 */
public class SingletonFactory {

    //装类和对象之间的映射
    //静态，全局
    private static Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {}

    //通过class拿到object
    public static <T> T getInstance(Class<T> clazz) {
        //通过class拿到object
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            //如果没有，就实例化，然后放进去
            if(instance == null) {
                try {
                    instance = clazz.newInstance();
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }

}
