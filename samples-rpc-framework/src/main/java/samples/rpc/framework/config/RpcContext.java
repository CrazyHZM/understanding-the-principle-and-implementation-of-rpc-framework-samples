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

package samples.rpc.framework.config;

import java.util.UUID;

/**
 * @author crazyhzm@apache.org
 */
public class RpcContext {
    private static ThreadLocal<String> uuid = ThreadLocal.withInitial(()->UUID.randomUUID().toString());

    private static String applicationName;

    private static String localIp;


    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        RpcContext.applicationName = applicationName;
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static void setLocalIp(String localIp) {
        RpcContext.localIp = localIp;
    }

    public static ThreadLocal<String> getUuid() {
        return uuid;
    }

    public static void setUuid(ThreadLocal<String> uuid) {
        RpcContext.uuid = uuid;
    }

}
