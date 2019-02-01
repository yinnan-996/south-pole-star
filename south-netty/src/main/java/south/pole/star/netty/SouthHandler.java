package south.pole.star.netty;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.provider.DispatcherFactory;
import south.pole.star.rpc.support.ProxyFactory;
import south.pole.star.rpc.support.SouthReponse;
import south.pole.star.rpc.support.SouthRequest;

@ChannelHandler.Sharable
public class SouthHandler extends SimpleChannelInboundHandler<SouthRequest> {

    private Logger LOGGER = LoggerFactory.getLogger(SouthHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SouthRequest southRequest) throws Exception {

        DispatcherFactory.getTaskDispatcher().execute(getTask(channelHandlerContext,southRequest),5);
    }

    private void callback(ChannelHandlerContext channelHandlerContext,SouthReponse southReponse,SouthRequest southRequest){
        channelHandlerContext.writeAndFlush(southReponse).addListener(successListener(channelHandlerContext,southRequest));
    }

    private ChannelFutureListener successListener(ChannelHandlerContext channelHandlerContext,SouthRequest southRequest){
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOGGER.debug("Send response for request " + southRequest.getRequestId());
            }
        };
    }


    private Runnable getTask(ChannelHandlerContext channelHandlerContext,SouthRequest southRequest) {
        LOGGER.info("[getTask] southRequest={}",southRequest);
        java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
                Object object =ProxyFactory.invoke(southRequest);
                SouthReponse southReponse = new SouthReponse();
                southReponse.setRequestId(southRequest.getRequestId());
                southReponse.setResult(object);
                callback(channelHandlerContext,southReponse,southRequest);
            }
        };
        LOGGER.info("[getTask] runnable={}",runnable);
        return runnable;
    }

}
