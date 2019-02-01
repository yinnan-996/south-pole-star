package south.pole.star.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.serialization.SerializationUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SouthDecode extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SouthDecode.class);
    private Class seriaClass;
    private Class objClass;

    public SouthDecode(Class seriaClass,Class objClass) {
        this.seriaClass = seriaClass;
        this.objClass = objClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        /*可读长度*/
        /*if(byteBuf.hasArray()) { // 处理堆缓冲区
            dataStr = new String(byteBuf.array(), byteBuf.arrayOffset() + byteBuf.readerIndex(), byteBuf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            dataStr = new String(bytes, 0, byteBuf.readableBytes());
        }*/
        String body = convertByteBufToString(byteBuf);
        LOGGER.info("SouthDecode dataStr={},seriaClass={},objClass={}",body,seriaClass,objClass);
        Object object = SerializationUtils.decode(body.getBytes(),seriaClass,objClass);
        list.add(object);

    }

    public String convertByteBufToString(ByteBuf buf) {
        //获取request的msg
        byte [] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        //去掉后面的回车换行符留下消息体并转换成string
        String body = null;
        try {
            body = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return body;


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
