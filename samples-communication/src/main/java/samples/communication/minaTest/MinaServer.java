package samples.communication.minaTest;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 15:39
 */
public class MinaServer {
    public static void init() throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(StandardCharsets.UTF_8,
                        LineDelimiter.WINDOWS.getValue(),
                        LineDelimiter.WINDOWS.getValue()))
        );
        acceptor.setHandler(new MinaServerHandler());
        acceptor.bind(new InetSocketAddress(3333));
    }
    public static void main(String[] args) throws IOException {
        MinaServer.init();
    }
}
