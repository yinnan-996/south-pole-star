package south.pole.star.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import south.pole.star.rpc.support.SouthRequest;
import south.pole.star.serialization.JavaSerialization;

import java.net.InetSocketAddress;


public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    private String host;

    private int port;

    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer();
    }

    public void start(){
        startServer();
    }

    private void startServer() {
        if (bossGroup == null && workerGroup == null) {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            final ByteBuf delimiter = Unpooled.copiedBuffer("$_$".getBytes());
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
//                                    .addLast(new DelimiterBasedFrameDecoder(8192,delimiter))
                                    .addLast(new SouthObjectDecode(SouthRequest.class))
                                    .addLast(new SouthObjectEncode(JavaSerialization.class))
                                    .addLast(new SouthHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = null;
            try {
                future = bootstrap.bind().sync();
                future.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("Server started on host={},port {}", host, port);
        }
    }
}
