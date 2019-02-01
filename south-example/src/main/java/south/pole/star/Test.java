package south.pole.star;

import south.pole.star.netty.RpcServer;
import south.pole.star.regist.RegistFactory;
import south.pole.star.regist.RegistService;
import south.pole.star.regist.ZookeeperRegistFactory;
import south.pole.star.server.SouthServer;

public class Test {

    public static void main(String[] args) {

        RegistService registService = ZookeeperRegistFactory.getRegistService(RegistFactory.REGIST_TYPE.ZOOKEEPER,"127.0.0.1",2181);
        SouthServer southServer = new SouthServer(registService,19999);
        HelloServiceImpl helloService = new HelloServiceImpl();
        southServer.regist(helloService);
        southServer.publish();
        RpcServer rpcServer = new RpcServer("127.0.0.1",19999);
        southServer.setRpcServer(rpcServer);
        southServer.startListen();

    }
}
