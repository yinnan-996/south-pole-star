package south.pole.star.rpc.support;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static <T> T getProxyInstance(Class<T> interfaceClass,String version){
        InvokerHandler<T> tInvokerHandler = new InvokerHandler<T>(interfaceClass,version);
        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},tInvokerHandler);
    }

    public static Object invoke(SouthRequest southRequest){
        try {
            Class<?> tClass = southRequest.getClass();
            Object object = ProxyFactory.getProxyInstance(tClass, southRequest.getVersin());
            Method method = null;
            method = object.getClass().getDeclaredMethod(southRequest.getMethodName(), southRequest.getParameterTypes());
            return method.invoke(object, southRequest.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object();
    }
}
