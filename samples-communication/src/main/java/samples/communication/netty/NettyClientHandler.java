package samples.communication.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2019-09-11 15:08
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client received data from server:" + msg);
    }
}
