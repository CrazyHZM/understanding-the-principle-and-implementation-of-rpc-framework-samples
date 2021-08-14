package samples.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import samples.proxy.FruitGrower;
import samples.proxy.Sales;

/**
 * @author: huazhongming
 * @date: Created in 2021/2/18 4:11 下午
 */
public class CGLibProxyTest {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(FruitGrower.class);
        enhancer.setCallback(new DemoMethodInterceptor());
        Sales proxy = (Sales)enhancer.create();
        proxy.sellFruit();
    }
}
