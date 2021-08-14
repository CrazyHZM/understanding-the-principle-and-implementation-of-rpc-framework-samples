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

import java.util.HashMap;
import java.util.Map;

/**
 * @author crazyhzm@apache.org
 */
public class TypeParseUtil {
    private static char DEFAULT_CHAR;

    /**
     * 转类型字符串到类型对象
     *
     * @param types
     * @return
     * @throws Throwable
     */
    public static Map<String, Object> parseTypeString2Class(String[] types, Object[] args) throws Throwable {
        Map<String, Object> result = new HashMap<>(types.length);
        Class<?>[] classTypes = new Class<?>[types.length];
        for (int i = 0; i < types.length; i++) {
            if ("byte".equals(types[i])) {
                classTypes[i] = byte.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(byte.class);
                }
            } else if ("short".equals(types[i])) {
                classTypes[i] = short.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(short.class);
                }
            } else if ("int".equals(types[i])) {
                classTypes[i] = int.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(int.class);
                }
            } else if ("long".equals(types[i])) {
                classTypes[i] = long.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(long.class);
                }
            } else if ("float".equals(types[i])) {
                classTypes[i] = float.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(float.class);
                }
            } else if ("double".equals(types[i])) {
                classTypes[i] = double.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(double.class);
                }
            } else if ("boolean".equals(types[i])) {
                classTypes[i] = boolean.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(boolean.class);
                }
            } else if ("char".equals(types[i])) {
                classTypes[i] = char.class;
                if (null == args[i]) {
                    args[i] = getBasicTypeDefaultValue(char.class);
                }
            } else {
                classTypes[i] = Class.forName(types[i]);
            }
        }

        result.put("classTypes", classTypes);
        result.put("args", args);
        return result;
    }


    /**
     * 返回基础类型默认值
     *
     * @return
     */
    public static Object getBasicTypeDefaultValue(Class<?> type) {
        if (byte.class.equals(type)) {
            return 0;
        } else if (short.class.equals(type)) {
            return 0;
        } else if (int.class.equals(type)) {
            return 0;
        } else if (long.class.equals(type)) {
            return 0;
        } else if (float.class.equals(type)) {
            return 0;
        } else if (double.class.equals(type)) {
            return 0;
        } else if (boolean.class.equals(type)) {
            return false;
        } else if (char.class.equals(type)) {
            return DEFAULT_CHAR;
        }
        return null;
    }
}
