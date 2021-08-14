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

package samples.rpc.framework.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author crazyhzm@apache.org
 */
public final class ReflectUtils {

    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';


    private ReflectUtils() {
    }


    /**
     * get name.
     * java.lang.Object[][].class => "java.lang.Object[][]"
     *
     * @param c class.
     * @return name.
     */
    public static String getName(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            }
            while (c.isArray());

            return c.getName() + sb.toString();
        }
        return c.getName();
    }


    /**
     * get class desc.
     * boolean[].class => "[Z"
     * Object.class => "Ljava/lang/Object;"
     *
     * @param c class.
     * @return desc.
     * @throws
     */
    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) {
                ret.append(JVM_VOID);
            } else if ("boolean".equals(t)) {
                ret.append(JVM_BOOLEAN);
            } else if ("byte".equals(t)) {
                ret.append(JVM_BYTE);
            } else if ("char".equals(t)) {
                ret.append(JVM_CHAR);
            } else if ("double".equals(t)) {
                ret.append(JVM_DOUBLE);
            } else if ("float".equals(t)) {
                ret.append(JVM_FLOAT);
            } else if ("int".equals(t)) {
                ret.append(JVM_INT);
            } else if ("long".equals(t)) {
                ret.append(JVM_LONG);
            } else if ("short".equals(t)) {
                ret.append(JVM_SHORT);
            }
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }


    /**
     * get method desc.
     * int do(int arg1) => "do(I)I"
     * void do(String arg1,boolean arg2) => "do(Ljava/lang/String;Z)V"
     *
     * @param m method.
     * @return desc.
     */
    public static String getDesc(final Method m) {
        StringBuilder ret = new StringBuilder(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            ret.append(getDesc(parameterTypes[i]));
        }
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }

    /**
     * get constructor desc.
     * "()V", "(Ljava/lang/String;I)V"
     *
     * @param c constructor.
     * @return desc
     */
    public static String getDesc(final Constructor<?> c) {
        StringBuilder ret = new StringBuilder("(");
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            ret.append(getDesc(parameterTypes[i]));
        }
        ret.append(')').append('V');
        return ret.toString();
    }

    /**
     * get method desc.
     * "(I)I", "()V", "(Ljava/lang/String;Z)V"
     *
     * @param m method.
     * @return desc.
     */
    public static String getDescWithoutMethodName(Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            ret.append(getDesc(parameterTypes[i]));
        }
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }


}