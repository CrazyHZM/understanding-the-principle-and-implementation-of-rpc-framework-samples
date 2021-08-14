package samples.proxy.javassist;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

/**
 * @author: huazhongming
 * @date: Created in 2021/2/18 5:21 下午
 */
public class DemoMethodHandler implements MethodHandler {

    @Override
    public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
        System.out.println("------Before selling fruits------");
        // 执行代理的目标对象的方法
        Object result = proceed.invoke(self, args);
        System.out.println("------After selling fruits------");
        return result;
    }
}
