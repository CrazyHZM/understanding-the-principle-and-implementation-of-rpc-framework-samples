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

import samples.rpc.framework.config.ReferenceConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author crazyhzm@apache.org
 */
public final class ReferenceUtil {

    private static final Map<String, ReferenceConfig> referenceConfigMap = new HashMap<String, ReferenceConfig>();

    private ReferenceUtil() {

    }

    public static void put(ReferenceConfig refrence) {
        referenceConfigMap.put(refrence.getName(), refrence);
    }

    public static ReferenceConfig get(String refrenceName) throws Exception {
        ReferenceConfig refrence = referenceConfigMap.get(refrenceName);
        if (refrence == null) {
            synchronized (referenceConfigMap) {
                refrence = referenceConfigMap.get(refrenceName);
                if (refrence == null) {
                    refrence = new ReferenceConfig();
                    refrence.setName(refrenceName);
                    refrence.init();
                    referenceConfigMap.put(refrenceName, refrence);
                }
            }
        }

        return refrence;
    }

}
