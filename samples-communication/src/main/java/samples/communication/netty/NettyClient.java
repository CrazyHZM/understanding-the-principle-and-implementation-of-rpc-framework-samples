package samples.communication.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2019-09-11 14:56
 */
public class NettyClient implements Runnable {

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                    pipeline.addLast("handler", new NettyClientHandler());
                }
            });

            ChannelFuture f = b.connect("127.0.0.1", 3333).sync();
            long cu = System.currentTimeMillis();
            f.channel().writeAndFlush("Hello Server!" + Thread.currentThread().getName() + ":--->:" + Thread.currentThread().getId());
            System.out.println(Thread.currentThread().getName() + ":--->:" + Thread.currentThread().getId() + "——————" + (System.currentTimeMillis() - cu));

            f.channel().closeFuture().sync();

        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 10; i++) {
            new Thread(new NettyClient(), "【this thread】 " + i).start();
        }
    }


}
