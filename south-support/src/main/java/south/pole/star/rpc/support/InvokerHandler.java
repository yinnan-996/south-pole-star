package south.pole.star.rpc.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerHandler<T> implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokerHandler.class);
     private Class<T> tClass;

     private String version;

    public InvokerHandler(Class<T> tClass,String version) {
        this.tClass = tClass;
        this.version = version!=null && version.length()>0?version:"1";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        T service = (T)ServiceImplFactory.getService(tClass,version);
        LOGGER.info("[invoke] tClass={},version={},service={},args={},method={}",tClass,version,service,args,method);
        return method.invoke(service,args);
    }
}
