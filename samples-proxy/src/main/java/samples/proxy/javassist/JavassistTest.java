package samples.proxy.javassist;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import samples.proxy.FruitGrower;
import samples.proxy.Sales;

import java.lang.reflect.Method;

/**
 * @author: huazhongming
 * @date: Created in 2021/2/18 5:22 下午
 */
public class JavassistTest {

    public static void main(String[] args) throws Exception{
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置被代理类
        proxyFactory.setSuperclass(FruitGrower.class);
        // 设置方法过滤器
        proxyFactory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method method) {
                return !"finalize".equals(method.getName());
            }
        });
        // 创建代理类
        Class c = proxyFactory.createClass();
        Sales proxy = (Sales) c.newInstance();
        // 设置方法调用处理器
        ((Proxy) proxy).setHandler(new DemoMethodHandler());
        // 调用方法
        proxy.sellFruit();
    }
}
