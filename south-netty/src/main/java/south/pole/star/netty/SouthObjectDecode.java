package south.pole.star.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.serialization.JavaSerialization;
import south.pole.star.serialization.SerializationUtils;

import java.util.List;

public class SouthObjectDecode extends ByteToMessageDecoder {

    private Class<?> clazz;

    private static final Logger LOGGER = LoggerFactory.getLogger(SouthObjectDecode.class);

    public SouthObjectDecode(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int length = byteBuf.readInt();
        // 读取data数据
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Object object = SerializationUtils.decode(data,JavaSerialization.class,clazz);
        list.add(object);

    }
}
