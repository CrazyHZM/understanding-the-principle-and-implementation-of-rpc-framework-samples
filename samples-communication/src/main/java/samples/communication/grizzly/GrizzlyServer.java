package samples.communication.grizzly;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
import org.glassfish.grizzly.utils.StringFilter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 16:09
 */
public class GrizzlyServer {

    public static void init() throws IOException {
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new StringFilter(StandardCharsets.UTF_8));

        filterChainBuilder.add(new GrizzlyServerFilter());
        TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();
        builder.setKeepAlive(true).setReuseAddress(false)
                .setIOStrategy(SameThreadIOStrategy.getInstance());
        TCPNIOTransport transport = builder.build();
        transport.setProcessor(filterChainBuilder.build());
        transport.bind(new InetSocketAddress(3333));
        transport.start();
        // 防止进程结束
        System.in.read();
    }
    public static void main(String[] args) throws IOException {
        GrizzlyServer.init();
    }
}
