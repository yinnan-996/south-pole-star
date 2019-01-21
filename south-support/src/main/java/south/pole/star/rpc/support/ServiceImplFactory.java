package south.pole.star.rpc.support;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceImplFactory {

    private static final ConcurrentHashMap<String,Object> serviceWareHouse = new ConcurrentHashMap<>();

    public static void add(Class className,String version,Object service){
        serviceWareHouse.put(className.getName()+":"+version,service);
    }

    public static void addService(Class className,String version,Object service){
        serviceWareHouse.put(className.getName()+":"+version,service);
    }

    public static <T>T getService(Class tClass, String version){
        return (T)serviceWareHouse.get(tClass.getName()+":"+version);
    }


}
