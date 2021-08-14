package samples.communication.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2019-09-11 14:58
 */
public class NettyServer {

    private static String IP = "127.0.0.1";
    private static int port = 3333;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(100);

    public static void init() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                //pipeline管理channel中的Handler
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(new NettyServerHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.bind(IP, port).sync();
        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> System.out.println("Complete connection."));
        channelFuture.channel().closeFuture().sync();
    }


    public static void main(String[] args) throws Exception {
        NettyServer.init();
    }

}
