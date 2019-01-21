package south.pole.star.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import south.pole.star.serialization.SerializationUtils;

import java.util.List;

public class SouthDecode extends ByteToMessageDecoder {

    private Class seriaClass;
    private Class objClass;

    public SouthDecode(Class seriaClass,Class objClass) {
        this.seriaClass = seriaClass;
        this.objClass = seriaClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        /*可读长度*/
        int length = byteBuf.readableBytes();
        byte[] data = new byte[length];
        SerializationUtils.decode(data,seriaClass.getClass(),objClass);

    }
}
