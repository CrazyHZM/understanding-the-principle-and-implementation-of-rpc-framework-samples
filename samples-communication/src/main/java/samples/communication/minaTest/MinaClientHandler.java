package samples.communication.minaTest;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 15:38
 */
public class MinaClientHandler extends IoHandlerAdapter {
    private final String values;

    public MinaClientHandler(String values) {
        this.values = values;
    }
    @Override
    public void sessionOpened(IoSession session) {
        session.write(values);
    }
}