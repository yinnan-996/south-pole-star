package south.pole.star.rpc.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerHandler<T> implements InvocationHandler {

     private Class<T> tClass;

     private String version;

    public InvokerHandler(Class<T> tClass,String version) {
        this.tClass = tClass;
        this.version = version!=null && version.length()>0?version:"1";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        T service = (T)ServiceImplFactory.getService(tClass,version);
        return method.invoke(service,args);
    }
}
