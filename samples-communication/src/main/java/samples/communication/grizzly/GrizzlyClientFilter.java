package samples.communication.grizzly;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 16:07
 */
public class GrizzlyClientFilter extends BaseFilter {

    @Override
    public NextAction handleRead(final FilterChainContext ctx) throws IOException {
        String serverMsg = ctx.getMessage();
        System.out.println("client received data from server: " + serverMsg);
        return ctx.getStopAction();
    }
}
