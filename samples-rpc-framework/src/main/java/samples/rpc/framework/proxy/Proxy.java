/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samples.rpc.framework.proxy;

import samples.rpc.framework.common.ClassUtils;
import samples.rpc.framework.common.ReflectUtils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author crazyhzm@apache.org
 */
public abstract class Proxy {
    private static final AtomicLong PROXY_CLASS_COUNTER = new AtomicLong(0);
    private static final String PACKAGE_NAME = Proxy.class.getPackage().getName();
    private static final Map<ClassLoader, Map<String, Object>> PROXY_CACHE_MAP = new WeakHashMap<ClassLoader, Map<String, Object>>();

    private static final Object PENDING_GENERATION_MARKER = new Object();

    protected Proxy() {
    }

    public static Proxy getProxy(Class<?> ics) {
        return getProxy(ClassUtils.getClassLoader(Proxy.class), ics);
    }


    public static Proxy getProxy(ClassLoader cl, Class<?> ics) {

        StringBuilder sb = new StringBuilder();
        // 接口的权限定名
        String itf = ics.getName();
        // 如果代理的不是接口，则抛出异常
        if (!ics.isInterface()) {
            throw new RuntimeException(itf + " is not a interface.");
        }

        Class<?> tmp = null;
        try {
            // 获得与itf对应的Class对象
            tmp = Class.forName(itf, false, cl);
        } catch (ClassNotFoundException e) {
        }

        // 如果通过类名获得的类型跟ics中的类型不一样，则抛出异常
        if (tmp != ics) {
            throw new IllegalArgumentException(ics + " is not visible from class loader");
        }
        sb.append(itf).append(';');

        String key = sb.toString();

        final Map<String, Object> cache;
        // 创建该类加载器的代理类缓存集合
        synchronized (PROXY_CACHE_MAP) {
            cache = PROXY_CACHE_MAP.computeIfAbsent(cl, k -> new HashMap<>());
        }

        Proxy proxy = null;
        // 无限循环获取代理对象，如果缓存没有命中，则判断是否为待处理的对象，如果是，则等待，否则将该占位对象加入缓存，结束循环。
        synchronized (cache) {
            do {
                Object value = cache.get(key);
                if (value instanceof Reference<?>) {
                    proxy = (Proxy) ((Reference<?>) value).get();
                    // 如果缓存中存在代理对象，则直接返回
                    if (proxy != null) {
                        return proxy;
                    }
                }

                if (value == PENDING_GENERATION_MARKER) {
                    try {
                        cache.wait();
                    } catch (InterruptedException e) {
                    }
                } else {
                    cache.put(key, PENDING_GENERATION_MARKER);
                    break;
                }
            }
            while (true);
        }

        // 代理类的id+1
        long id = PROXY_CLASS_COUNTER.getAndIncrement();
        // 包名
        String pkg = null;
        ClassGenerator ccp = null, ccm = null;
        try {
            // 创建类生成器
            ccp = ClassGenerator.newInstance(cl);

            Set<String> worked = new HashSet<>();
            // 方法集合
            List<Method> methods = new ArrayList<>();
            // 只能代理的接口是否被Public修饰
            if (!Modifier.isPublic(ics.getModifiers())) {
                // 获得该类的包名
                String npkg = ics.getPackage().getName();
                if (pkg == null) {
                    pkg = npkg;
                } else {
                    if (!pkg.equals(npkg)) {
                        throw new IllegalArgumentException("non-public interfaces from different packages");
                    }
                }
            }
            // 类生成器赋值mInterfaces
            ccp.addInterface(ics);

            // 遍历接口的所有方法
            for (Method method : ics.getMethods()) {
                // 获得方法的描述，比如void do(String arg1,boolean arg2) => "do(Ljava/lang/String;Z)V"
                String desc = ReflectUtils.getDesc(method);
                // 如果集合中存在，则跳过
                if (worked.contains(desc)) {
                    continue;
                }
                // 如果该方法是static方法，则跳过
                if (ics.isInterface() && Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                // 加入集合
                worked.add(desc);

                int ix = methods.size();
                // 获得方法返回类型
                Class<?> rt = method.getReturnType();
                // 获得方法参数类型
                Class<?>[] pts = method.getParameterTypes();

                // 拼接代码
                StringBuilder code = new StringBuilder("Object[] args = new Object[").append(pts.length).append("];");
                // 为每一个参数生成一句代码，比如例如args[0] = ($w)$1;
                for (int j = 0; j < pts.length; j++) {
                    code.append(" args[").append(j).append("] = ($w)$").append(j + 1).append(";");
                }
                code.append(" Object ret = handler.invoke(this, methods[").append(ix).append("], args);");
                // 如果方法不是void类型，则拼接return ret;
                if (!Void.TYPE.equals(rt)) {
                    code.append(" return ").append(asArgument(rt, "ret")).append(";");
                }

                // 加入到方法集合
                methods.add(method);
                // 加入到类生成器到缓存中
                ccp.addMethod(method.getName(), method.getModifiers(), rt, pts, method.getExceptionTypes(), code.toString());
            }

            // 如果包名称为空，则使用默认值
            if (pkg == null) {
                pkg = PACKAGE_NAME;
            }

            // 拼接类名
            String pcn = pkg + ".proxy" + id;
            ccp.setClassName(pcn);
            // 添加静态字段Method[] methods
            ccp.addField("public static java.lang.reflect.Method[] methods;");
            // 添加实例对象InvokerInvocationHandler hanler
            ccp.addField("private " + InvocationHandler.class.getName() + " handler;");
            // 添加参数为InvokerInvocationHandler的构造器
            ccp.addConstructor(Modifier.PUBLIC, new Class<?>[]{InvocationHandler.class}, new Class<?>[0], "handler=$1;");
            // 添加默认无参构造器
            ccp.addDefaultConstructor();
            // 使用toClass方法生成对应的字节码
            Class<?> clazz = ccp.toClass();
            clazz.getField("methods").set(null, methods.toArray(new Method[0]));

            // 生成类名
            String fcn = Proxy.class.getName() + id;
            // 创建类类生成器
            ccm = ClassGenerator.newInstance(cl);
            ccm.setClassName(fcn);
            // 创建无参构造函数
            ccm.addDefaultConstructor();
            // 设置父类为Proxy
            ccm.setSuperClass(Proxy.class);
            // 添加一个创建接口服务类到方法
            ccm.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " h){ return new " + pcn + "($1); }");
            // 获得class对象
            Class<?> pc = ccm.toClass();
            // 生成代理类
            proxy = (Proxy) pc.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (ccp != null) {
                ccp.release();
            }
            if (ccm != null) {
                ccm.release();
            }
            synchronized (cache) {
                if (proxy == null) {
                    cache.remove(key);
                } else {
                    cache.put(key, new WeakReference<Proxy>(proxy));
                }
                cache.notifyAll();
            }
        }
        return proxy;
    }


    private static String asArgument(Class<?> cl, String name) {
        if (cl.isPrimitive()) {
            if (Boolean.TYPE == cl) {
                return name + "==null?false:((Boolean)" + name + ").booleanValue()";
            }
            if (Byte.TYPE == cl) {
                return name + "==null?(byte)0:((Byte)" + name + ").byteValue()";
            }
            if (Character.TYPE == cl) {
                return name + "==null?(char)0:((Character)" + name + ").charValue()";
            }
            if (Double.TYPE == cl) {
                return name + "==null?(double)0:((Double)" + name + ").doubleValue()";
            }
            if (Float.TYPE == cl) {
                return name + "==null?(float)0:((Float)" + name + ").floatValue()";
            }
            if (Integer.TYPE == cl) {
                return name + "==null?(int)0:((Integer)" + name + ").intValue()";
            }
            if (Long.TYPE == cl) {
                return name + "==null?(long)0:((Long)" + name + ").longValue()";
            }
            if (Short.TYPE == cl) {
                return name + "==null?(short)0:((Short)" + name + ").shortValue()";
            }
            throw new RuntimeException(name + " is unknown primitive type.");
        }
        return "(" + ReflectUtils.getName(cl) + ")" + name;
    }

    abstract public Object newInstance(InvocationHandler handler);

}
