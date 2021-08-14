package samples.communication.minaTest;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 15:37
 */
public class MinaClient {

    public static void main(String[] args) {
        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(30000);
        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(StandardCharsets.UTF_8,
                        LineDelimiter.WINDOWS.getValue(),
                        LineDelimiter.WINDOWS.getValue())));

        connector.setHandler(new MinaClientHandler("Hello Server!"));

        connector.connect(new InetSocketAddress("127.0.0.1", 3333));

    }

}
