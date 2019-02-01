package south.pole.star.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.serialization.SerializationUtils;

public class SouthObjectEncode extends MessageToByteEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SouthObjectEncode.class);
    private Class clazz;

    public SouthObjectEncode(Class clazz){
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        LOGGER.info("[SouthObjectEncode] t={}",o);
        byte[] bt = SerializationUtils.encode(o,clazz);
        LOGGER.info("[SouthObjectEncode] o={},bt={}",o,bt);
        byteBuf.writeInt(bt.length);
        byteBuf.writeBytes(bt);
    }
}
