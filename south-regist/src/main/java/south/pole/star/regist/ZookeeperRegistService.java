package south.pole.star.regist;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZookeeperRegistService implements RegistService {

    /**连接注册中心**/
    private String host;

    private int port;

    private CuratorFramework client;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistService.class);

    private ConcurrentMap<RegistMeta,RegisterState> registerMetaMap = new ConcurrentHashMap<RegistMeta,RegisterState>();

    private final ExecutorService registerExecutor = Executors.newSingleThreadExecutor();

    protected ConcurrentMap<RegistMeta,RegisterState> getRegisterMetaMap() {
        return registerMetaMap;
    }


    public ZookeeperRegistService(String host, int port) {
        this.host = host;
        this.port = port;
        init(host+":"+port);
    }

    @Override
    public void provider(Object serviceProvider) {
        try {
            RegistMeta registMeta = new RegistMeta();
            registMeta.setHost(this.host);
            registMeta.setPort(19999);
            registMeta.setClassName(serviceProvider.getClass().getInterfaces()[0].getName());
            registMeta.setVersion("1.0.v");
            queue.add(registMeta);
        } catch (Exception e) {
            LOGGER.error("[provider] serviceProvider={}",serviceProvider);
        }
    }


    @Override
    public void regist() {
            LOGGER.info("regist");
            registerExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    RegistMeta registMeta = null;
                    try {
                         registMeta = queue.take();
                        String directory = String.format("/south/provider/%s/%s",
                                registMeta.getClassName(),
                                registMeta.getVersion());
                        LOGGER.info("regist,directory={}",directory);
                        String nodeName = client.create().creatingParentsIfNeeded()
                                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                                .forPath(directory,(registMeta.getHost()+":"+registMeta.getPort()).getBytes("UTF-8"));
                        LOGGER.info("regist,nodeName={}",nodeName);
                        registerMetaMap.putIfAbsent(registMeta,RegisterState.DONE);
                    } catch (Exception e) {
                        LOGGER.error("[regist] error={}",e);
                        try {
                            if(registMeta!=null){
                                Thread.sleep(1000L);
                                queue.add(registMeta);
                            }
                        } catch (InterruptedException e1) {
                            LOGGER.error("[retry regist] error={}",e);
                        }

                    }
                }
            });

    }
    @Override
    public void unRegist(RegistMeta registMeta) {

    }

    @Override
    public List<RegistMeta> findService(Class tClass,String version) {
        String directory = String.format("/south/provider/%s/%s",
                tClass.getName(),
                version);
        List<RegistMeta> registerMetaList = new ArrayList();
        try {
            List<String> paths = client.getChildren().forPath(directory);
            for (String p : paths) {
                registerMetaList.add(parseRegisterMeta(String.format("%s/%s", directory, p)));
            }
        } catch (Exception e) {
            LOGGER.error("Lookup service error: {} ",e);
        }
        return registerMetaList;
    }
    /**
     *
     * @param data  /-/-/providername/version/host:port
     * @return
     */
    private RegistMeta parseRegisterMeta(String data){
        String[] sz = data.substring(1).split("/");
        RegistMeta meta = new RegistMeta();
        meta.setClassName(sz[2]);
        meta.setVersion(sz[3]);
        String host_port = sz[4];
        String host = host_port.split(":")[0];
        int port = Integer.valueOf(host_port.split(":")[1]);
        meta.setHost(host);
        meta.setPort(port);
        return meta;
    }
    private void init(String url){
        try {
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .connectString(url)
                    .retryPolicy(new RetryNTimes(1, 1000))
                    .connectionTimeoutMs(TIME_OUT);
            client = builder.build();
            client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework client, ConnectionState state) {
                    registerExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            if (state == ConnectionState.LOST) {
                                LOGGER.info("lost session with zookeeper");
                                reconnect(client);
                            } else if (state == ConnectionState.CONNECTED) {
                                LOGGER.info("connected with zookeeper");
                            } else if (state == ConnectionState.RECONNECTED) {
                                LOGGER.info("reconnected with zookeeper");
                                reconnect(client);
                            }
                        }
                    });
                }
            });
            client.start();
        }catch (Exception ex){

        }
    }

    private void reconnect(CuratorFramework client){
        try {
            while (true){
                //手动重连
                boolean flag=client.getZookeeperClient().blockUntilConnectedOrTimedOut();
                if (flag){
                    //重新添加节点
                    clearListener();
                    regist();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearListener(){
        ListenerContainer<ConnectionStateListener> list=(ListenerContainer<ConnectionStateListener>) client.getConnectionStateListenable();
        list.clear();
    }









}
