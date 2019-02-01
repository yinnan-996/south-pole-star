package south.pole.star.netty.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.rpc.support.SouthReponse;
import south.pole.star.rpc.support.SouthRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ClientProxy<T> implements InvocationHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProxy.class);
    private Class<T> clazz;
    private String version;

    public ClientProxy(Class<T> clazz,String version) {
        this.clazz = clazz;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SouthReponse southReponse=null;
        SouthRequest request = new SouthRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(this.clazz);
        request.setMethodName(method.getName());
        request.setVersin(version);
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        SouthRequestHandler southRequestHandler = ConnectManage.getInstance().select(request);
        if(southRequestHandler!=null){
            southReponse = southRequestHandler.sendRequest(request);
            if(southReponse!=null){
                return southReponse.getResult();
            }
        }
        LOGGER.error("【ClientProxy】error,method={},args={},southReponse={}",method,args,southReponse);
        return null;
    }
}
