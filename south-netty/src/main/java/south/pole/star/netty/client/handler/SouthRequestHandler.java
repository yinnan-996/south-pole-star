package south.pole.star.netty.client.handler;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.rpc.support.SouthReponse;
import south.pole.star.rpc.support.SouthRequest;
import south.pole.star.rpc.support.SouthResultFuture;

import java.util.concurrent.ConcurrentHashMap;


public class SouthRequestHandler extends SimpleChannelInboundHandler<SouthReponse> {

//    private ConcurrentHashMap<String,ChannelFuture> reponseFuture = new ConcurrentHashMap<String,ChannelFuture>();
//    private  ConcurrentHashMap<String,SouthReponse> reponseMap = new ConcurrentHashMap<String,SouthReponse>();

    private ConcurrentHashMap <String,SouthResultFuture> futureMap =  new ConcurrentHashMap<>();


    private Logger LOGGER = LoggerFactory.getLogger(SouthRequestHandler.class);
    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SouthReponse southReponse) throws Exception {
        String requestId = southReponse.getRequestId();
        LOGGER.info("[SouthRequestHandler] channelRead0 southReponse={}",southReponse);

        SouthResultFuture southResultFuture = futureMap.get(southReponse.getRequestId());
        southResultFuture.done(southReponse);

        /*reponseMap.put(southReponse.getRequestId(),southReponse);

        ChannelFuture  channelFuture = reponseFuture.get(requestId);
        LOGGER.info("[sendRequest] operationComplete channelRead0={}",channelFuture);
        synchronized (channelFuture){
            channelFuture.notify();
        }
        LOGGER.info("[sendRequest] operationComplete notify,requestId={}",requestId);
        reponseFuture.remove(requestId);*/

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOGGER.info("[SouthRequestHandler] exceptionCaught close error={}",cause);
        channel.close();
    }

    public SouthReponse sendRequest(SouthRequest southRequest){
        LOGGER.info("[SouthRequestHandler] sendRequest={}",southRequest);
        ChannelFuture channelFuture = this.channel.writeAndFlush(southRequest);

        SouthResultFuture southResultFuture = new SouthResultFuture();

        futureMap.put(southRequest.getRequestId(),southResultFuture);

        SouthReponse southReponse = southResultFuture.get();
        futureMap.remove(southRequest.getRequestId());

       /* SouthReponse southReponse=null;
        final ChannelFuture[] channelFutureAwait = new ChannelFuture[1];
        final Object mutex=new Object();
        ChannelFuture channelFuture1 = channelFuture.addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                reponseFuture.put(southRequest.getRequestId(),channelFuture);
                LOGGER.info("[sendRequest] operationComplete channelFuture={}",channelFuture);
                channelFutureAwait[0] = channelFuture;
                synchronized (mutex){
                    LOGGER.info("[sendRequest] operationComplete notify mutex={}",mutex);
                    mutex.notify();
                }


            }
        });
        try {
            if(channelFutureAwait.length==0 || channelFutureAwait[0]==null){
                LOGGER.info("[sendRequest] operationComplete notify,waitBefore,requestId={}",southRequest.getRequestId());
                synchronized (mutex){
                    mutex.wait();
                }
            }
            LOGGER.info("[sendRequest] operationComplete notify channelFutureAwait wait mutex={}",mutex);
            synchronized (channelFutureAwait[0]){
                channelFutureAwait[0].wait(3000L);
            }
            LOGGER.info("[sendRequest] operationComplete notify,waitEnd,requestId={}",southRequest.getRequestId());
            southReponse = reponseMap.get(southRequest.getRequestId());
            reponseMap.remove(southRequest.getRequestId());
            LOGGER.info("[sendRequest] result={}",southReponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return southReponse;

    }

    public void close(){
        if(channel!=null){
            LOGGER.info("[SouthRequestHandler] close");
            channel.close();
        }
    }
}
