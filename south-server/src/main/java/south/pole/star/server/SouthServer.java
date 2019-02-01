package south.pole.star.server;

import south.pole.star.netty.RpcServer;
import south.pole.star.regist.RegistService;
import south.pole.star.rpc.support.ServiceImplFactory;

public class SouthServer {


    private RegistService registService;

    private int port;

    private RpcServer rpcServer;


    public SouthServer(RegistService registService, int port) {
        this.registService = registService;
        this.port = port;
    }
    /**
     * 注册到本地容器
     * @param serviceImpl
     */
    public void regist(Object serviceImpl){
        ServiceImplFactory.add(serviceImpl.getClass().getInterfaces()[0],"1.0.v",serviceImpl);
        registService.provider(serviceImpl);
    }
    /**
     * 注册到注册中心
     */
    public void publish(){
        registService.regist();
    }

    public void startListen(){
        rpcServer.start();
    }


    public RpcServer getRpcServer() {
        return rpcServer;
    }

    public void setRpcServer(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    public RegistService getRegistService() {
        return registService;
    }

    public void setRegistService(RegistService registService) {
        this.registService = registService;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

