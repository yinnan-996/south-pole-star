package south.pole.star.rpc.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceImplFactory {

    private static final ConcurrentHashMap<String,Object> serviceWareHouse = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceImplFactory.class);

    public static void add(Class className,String version,Object service){
        LOGGER.info("[ServiceImplFactory] className={},version={},service={}",className.getName(),version,service);
        serviceWareHouse.put(className.getName()+":"+version,service);
    }

    public static void addService(Class className,String version,Object service){
        serviceWareHouse.put(className.getName()+":"+version,service);
    }

    public static <T>T getService(Class tClass, String version){
        LOGGER.info("[ServiceImplFactory] className={},version={}",tClass.getName(),version);
        return (T)serviceWareHouse.get(tClass.getName()+":"+version);
    }


}
