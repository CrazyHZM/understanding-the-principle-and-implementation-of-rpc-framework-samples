package samples.proxy.jdk;

import samples.proxy.Sales;
import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: huazhongming
 * @date: Created in 2021/2/18 10:43 上午
 */
public class GenerateProxyClassTest {
    public static void main(String[] args) {
        String path = "$Proxy0.class";
        byte[] classFile = ProxyGenerator.generateProxyClass("$Proxy0", new Class[] { Sales.class });
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(classFile);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
