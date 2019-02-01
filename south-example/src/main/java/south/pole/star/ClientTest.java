package south.pole.star;

import south.pole.star.netty.client.handler.ConnectManage;
import south.pole.star.netty.client.handler.RpcClient;
import south.pole.star.regist.ZookeeperServiceDiscovery;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest {

    public static void main(String[] args) throws InterruptedException {

        ZookeeperServiceDiscovery serviceDiscovery = new ZookeeperServiceDiscovery("127.0.0.1",2181);

        Executor executor = Executors.newFixedThreadPool(4);
        try {
            serviceDiscovery.watchNode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectManage.await();
        RpcClient client = new RpcClient("127.0.0.1",19999);

        HelloService helloService = client.create(HelloService.class,"1.0.v");

        try {

            for(int i=0;i<1000;i++){
                ((ExecutorService) executor).submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int k=0;k<10000;k++){
                            for(int i=0;i<10;i++){
                                String ss = helloService.helloWorld("client test");
                                System.out.println("**********"+ss+"*****************");
                            }
                            try {
                                Thread.sleep(6000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }



    }
}
