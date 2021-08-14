package samples.communication.minaTest;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020-09-27 15:40
 */
public class MinaServerHandler extends IoHandlerAdapter {

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String str = message.toString();
        System.out.println("The message received is " + str);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("server session created");
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("server session Opened");
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("server session Closed");
        super.sessionClosed(session);
    }

}
