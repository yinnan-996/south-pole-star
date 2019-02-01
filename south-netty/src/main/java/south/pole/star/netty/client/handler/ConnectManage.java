package south.pole.star.netty.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.netty.SouthObjectDecode;
import south.pole.star.netty.SouthObjectEncode;
import south.pole.star.rpc.support.SouthReponse;
import south.pole.star.rpc.support.SouthRequest;
import south.pole.star.serialization.JavaSerialization;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class ConnectManage {

    private static ConnectManage connectManage;
    //classpath-ip
    private ConcurrentHashMap<String,Set<String>> pathAddressMap = new ConcurrentHashMap<>();
    //ip-handler
    private ConcurrentHashMap<String,SouthRequestHandler> ipHandlerMap = new ConcurrentHashMap<>();

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectManage.class);

    public static CountDownLatch countDownLatch = null;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    public static ConnectManage getInstance(){
        if(connectManage==null){
            synchronized (ConnectManage.class){
                if(connectManage==null){
                    connectManage = new ConnectManage();
                }
            }
        }
        return connectManage;
    }

    private ConnectManage() {
    }

    /**
     *
     * @param pathAndAddressList path-host:port
     */
    public void updateSouthHandler(int type,String pathAndAddressList){

        LOGGER.info("[updateSouthHandler,type={},pathAndAddressList={}",type,pathAndAddressList);
        String[] ars = pathAndAddressList.split("-");
        String classPath = ars[0];
        String host = ars[1];
        switch (type){
            case 1:
                if(ipHandlerMap.get(host)==null){
                    LOGGER.info("[updateSouthHandler] classPath={},host={}",classPath,host);
                    connectServerNode(classPath,new InetSocketAddress(host.split(":")[0],Integer.valueOf(host.split(":")[1])));
                }
                break;
            case -1:
                closeHandler(pathAndAddressList);
                break;
        }

    }

    private void closeHandler(String pathAndAddressList){
        String[] ars = pathAndAddressList.split("-");
        String classPath = ars[0];
        String host = ars[1];
        SouthRequestHandler southRequestHandler = ipHandlerMap.get(host);
        if(southRequestHandler!=null){
            Set<String> set = pathAddressMap.get(classPath);
            if(set!=null){
                set.remove(host);
            }
            ipHandlerMap.remove(pathAndAddressList);
            southRequestHandler.close();
        }
    }

    private void connectServerNode(String classPath,final InetSocketAddress remotePeer) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>(){
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline channelPipeline = socketChannel.pipeline();
                                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
//                                channelPipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                                channelPipeline.addLast(new SouthObjectDecode(SouthReponse.class));
                                channelPipeline.addLast(new SouthObjectEncode(JavaSerialization.class));
                                channelPipeline.addLast(new SouthRequestHandler());
                            }
                        });

                ChannelFuture channelFuture = b.connect(remotePeer);
                LOGGER.info("[connectServerNode]classPath={},remotePeer={}",classPath,remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            LOGGER.debug("Successfully connect to remote server. remote peer = " + remotePeer);
                            SouthRequestHandler handler = channelFuture.channel().pipeline().get(SouthRequestHandler.class);
                            addCacheHandler(remotePeer,handler);
                            addPathAddressCache(classPath,remotePeer);
                            countDownLatch.countDown();
                        }
                    }
                });
            }
        });
    }


    private void addCacheHandler(InetSocketAddress remotePeer,SouthRequestHandler handler){
        ipHandlerMap.put(remotePeer.getHostString()+":"+remotePeer.getPort(),handler);
    }

    private void addPathAddressCache(String path,InetSocketAddress inetSocketAddress){
        LOGGER.info("addPathAddressCache,path={},inetSocketAddress={}",path,inetSocketAddress);
        Set<String> set = pathAddressMap.get(path);
        if(set==null){
            set = new HashSet<String>();
        }
        set.add(inetSocketAddress.getHostString()+":"+inetSocketAddress.getPort());
        pathAddressMap.put(path,set);
        LOGGER.info("addPathAddressCache end,path={},inetSocketAddress={}",path,set);
    }

    public SouthRequestHandler select(SouthRequest southRequest) throws Exception{
        String path = southRequest.getClassName().getName()+"/"+southRequest.getVersin();
        LOGGER.info("select hanlder path={},pathAddressMap={}",path,pathAddressMap==null?0:pathAddressMap.size());
        Iterator<String> iterator = pathAddressMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            LOGGER.info("select hanlder key={},value={}",key,pathAddressMap.get(key));
        }
        Set<String> set = pathAddressMap.get(path);
        if(set==null || set.size()==0){
            throw new Exception("no service");
        }
        int size = set.size();
        String[] ipArrays = new String[size];
        set.toArray(ipArrays);
        int num = (int)(Math.random()*size);
        return ipHandlerMap.get(ipArrays[num]);
    }

    public static void await() throws InterruptedException {
        countDownLatch.await();
    }
}
