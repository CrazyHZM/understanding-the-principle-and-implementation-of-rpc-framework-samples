package samples.proxy.jdk;

import samples.proxy.FruitGrower;
import samples.proxy.Sales;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author: huazhongming
 * @date: Created in 2021/2/15 10:38 下午
 */
public class JdkProxyTest {
    public static void main(String[] args) throws Exception {
        FruitGrower fruitGrower = new FruitGrower();
        ClassLoader classLoader = fruitGrower.getClass().getClassLoader();
        Class<?>[] interfaces = fruitGrower.getClass().getInterfaces();
        InvocationHandler invocationHandler = new DemoInvocationHandler(fruitGrower);
        Sales proxy = (Sales) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        proxy.sellFruit();
    }
}
