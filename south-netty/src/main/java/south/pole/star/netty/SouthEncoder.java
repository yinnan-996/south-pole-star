package south.pole.star.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import south.pole.star.serialization.Serialization;
import south.pole.star.serialization.SerializationUtils;

@ChannelHandler.Sharable
public class SouthEncoder extends MessageToByteEncoder {

    private Class seriaClass;
    public SouthEncoder(Class<? extends Serialization> seriaClass){
        this.seriaClass = seriaClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws Exception {
        byte[] bytes = SerializationUtils.encode(obj,seriaClass);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
