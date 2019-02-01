package south.pole.star.netty.client.handler;

import java.lang.reflect.Proxy;

public class RpcClient {

    private String host;

    private int port;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public  <T> T create(Class<T> interfaceClass,String version){
        return (T)Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ClientProxy(interfaceClass,version));
    }
}
