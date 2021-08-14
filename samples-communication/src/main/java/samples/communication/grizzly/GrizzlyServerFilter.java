package samples.communication.grizzly;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 16:13
 */
public class GrizzlyServerFilter extends BaseFilter {

    @Override
    public NextAction handleRead(FilterChainContext ctx) {
        final Object message = ctx.getMessage();
        System.out.println((String) message);
        return ctx.getStopAction();
    }
}
