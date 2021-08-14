package samples.communication.grizzly;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 16:01
 */
public class GrizzlyClient {

    public static void main(String[] args) throws IOException,
            ExecutionException, InterruptedException, TimeoutException {

        Connection connection;
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new StringFilter(StandardCharsets.UTF_8));
        filterChainBuilder.add(new GrizzlyClientFilter());
        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
        transport.setProcessor(filterChainBuilder.build());
        transport.start();
        Future<Connection> future = transport.connect("127.0.0.1", 3333);
        connection = future.get(10, TimeUnit.SECONDS);
        if (connection != null && connection.isOpen()) {
            System.out.println("Connection Success!");
        }
        if (connection != null) {
            connection.write("Hello, Server.");
        }

    }
}
