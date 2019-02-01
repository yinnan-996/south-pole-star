package south.pole.star.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.serialization.Serialization;
import south.pole.star.serialization.SerializationUtils;

@ChannelHandler.Sharable
public class SouthEncoder extends MessageToByteEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SouthEncoder.class);

    private Class seriaClass;
    public SouthEncoder(Class<? extends Serialization> seriaClass){
        this.seriaClass = seriaClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws Exception {

        LOGGER.info("[SouthEncoder] encode,obj={}",obj);
        byte[] bytes = SerializationUtils.encode(obj,seriaClass);
        LOGGER.info("[SouthEncoder] encode,obj={},bytes={},bytes.length={}",obj,bytes,bytes.length);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
