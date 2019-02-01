package south.pole.star.regist;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.netty.client.handler.ConnectManage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZookeeperServiceDiscovery {

    /**连接注册中心**/
    private String host;

    private int port;


    private CuratorFramework client;

    private final ExecutorService discoveryExecutor = Executors.newSingleThreadExecutor();

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private List<String> dataList= new ArrayList<>();

    public ZookeeperServiceDiscovery(String host, int port) {
        this.host = host;
        this.port = port;
        init(host+":"+port);
    }

    private void init(String url){
        try {

            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .connectString(url)
                    .retryPolicy(new RetryNTimes(1, 1000))
                    .connectionTimeoutMs(30000);
            client = builder.build();


            /*client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework client, ConnectionState state) {
                    discoveryExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            if (state == ConnectionState.LOST) {
                                LOGGER.info("lost session with zookeeper");
                            } else if (state == ConnectionState.CONNECTED) {
                                LOGGER.info("connected with zookeeper");
                            } else if (state == ConnectionState.RECONNECTED) {
                                LOGGER.info("reconnected with zookeeper");
                            }
                        }
                    });
                }
            });*/
            client.start();
            int size = 0;
            List<String> path = client.getChildren().forPath("/south/provider");
            for (String p: path) {
                LOGGER.info("[ZookeeperServiceDiscovery init] patj={}",p);
                List<String> path1 = client.getChildren().forPath("/south/provider/"+p);
                for(String p1 :path1){
                    LOGGER.info("[ZookeeperServiceDiscovery p1-utf={}",p1);
                    getDataNode(client,p,p1);
                    size++;
                }
            }
            ConnectManage.countDownLatch = new CountDownLatch(size);
        }catch (Exception ex){
            LOGGER.error("ZookeeperServiceDiscovery error={}",ex);
        }
    }

    /**
     * 获取指定节点中信息
     * @throws Exception
     */
    private  void getDataNode(CuratorFramework client,String classPath,String versionPath) throws Exception{
        String path = "/south/provider/"+classPath+"/"+versionPath;
        String v = versionPath.substring(0,5);
        Stat stat = client.checkExists().forPath(path);
        byte[] datas = client.getData().forPath(path);
        String cacheClassPath = classPath+"/"+v;
        String host = new String(datas);
        LOGGER.info("[ZookeeperServiceDiscovery cacheClassPath={},data={}",cacheClassPath,host);
        //path-host:port
        updateServiceAddressHandler(1,cacheClassPath+"-"+host);
    }



    private void reconnect(CuratorFramework client){
        try {
            while (true){
                //手动重连
                boolean flag=client.getZookeeperClient().blockUntilConnectedOrTimedOut();
                if (flag){
                    //重新添加节点
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateServiceAddressHandler(int type,String pathAndHost){
        ConnectManage.getInstance().updateSouthHandler(type,pathAndHost);
    }

    /*public void watchNode() throws Exception{
        final PathChildrenCache childrenCache = new PathChildrenCache(client, "/south/provider", true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                            throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                LOGGER.info("CHILD_ADDED: paht={}" + event.getData().getPath());
                                String pathValue = event.getData().getPath()+"-"+new String(event.getData().getData(),"UTF-8");
                                LOGGER.info("CHILD_REMOVED value={}",pathValue);
                                if(!dataList.contains(pathValue)){
                                    dataList.add(pathValue);
                                }
                                updateServiceAddressHandler(1,pathValue);
                                break;
                            case CHILD_REMOVED:
                                LOGGER.info("CHILD_REMOVED: paht={}" + event.getData().getPath());
                                String value2 = event.getData().getPath()+"-"+new String(event.getData().getData(),"UTF-8");
                                LOGGER.info("CHILD_REMOVED value={}",value2);
                                if(dataList.contains(value2)){
                                    dataList.remove(value2);
                                }
                                updateServiceAddressHandler(-1,value2);
                                break;
                            case CHILD_UPDATED:
                                LOGGER.info("CHILD_UPDATED: " + event.getData().getPath());
                                break;
                            default:
                                break;
                        }
                    }
                },
                discoveryExecutor
        );
    }*/
    public void watchNode() throws Exception{
        PathChildrenCache cached = pathChildrenCache(client,"/south/provider",false);
        List<ChildData> datas = cached.getCurrentData();
        for (ChildData data : datas) {
            System.out.println("pathcache:{" + data.getPath() + ":" + new String(data.getData())+"}");
        }


    }

    public  PathChildrenCache pathChildrenCache(CuratorFramework client, String path, Boolean cacheData) throws Exception {
        final PathChildrenCache cached = new PathChildrenCache(client, path, cacheData);
        cached.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type eventType = event.getType();
                switch (eventType) {
                    case CONNECTION_RECONNECTED:
                        cached.rebuild();
                        break;
                    case CONNECTION_SUSPENDED:
                    case CONNECTION_LOST:
                        System.out.println("Connection error,waiting...");
                        break;
                    case CHILD_ADDED:
                        LOGGER.info("CHILD_ADDED: paht={}" + event.getData().getPath());
                        String pathValue = event.getData().getPath()+"-"+new String(event.getData().getData(),"UTF-8");
                        LOGGER.info("CHILD_REMOVED value={}",pathValue);
                        if(!dataList.contains(pathValue)){
                            dataList.add(pathValue);
                        }
                        updateServiceAddressHandler(1,pathValue);
                        break;
                     case CHILD_REMOVED:
                        LOGGER.info("CHILD_REMOVED: paht={}" + event.getData().getPath());
                        String value2 = event.getData().getPath()+"-"+new String(event.getData().getData(),"UTF-8");
                        LOGGER.info("CHILD_REMOVED value={}",value2);
                        if(dataList.contains(value2)){
                            dataList.remove(value2);
                        }
                        updateServiceAddressHandler(-1,value2);
                        break;
                    case CHILD_UPDATED:
                        LOGGER.info("CHILD_UPDATED: " + event.getData().getPath());
                        break;
                    default:
                        System.out.println("PathChildrenCache changed : {path:" + event.getData().getPath() + " data:" +
                                new String(event.getData().getData()) + "}");
                }
            }
        });

        return cached;
    }

}
