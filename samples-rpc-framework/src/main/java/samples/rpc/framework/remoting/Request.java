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

package samples.rpc.framework.remoting;

import samples.rpc.framework.config.ServiceConfig;

import java.io.Serializable;

/**
 * @author crazyhzm@apache.org
 */
public class Request implements Serializable {

    /**
     * 唯一键
     */
    private String requestId;

    /**
     * 服务名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private String[] types;

    /**
     * 参数
     */
    private Object[] args;

    /**
     * 客户端应用名称
     */
    private String clientApplicationName;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 服务
     */
    private ServiceConfig service;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getClientApplicationName() {
        return clientApplicationName;
    }

    public void setClientApplicationName(String clientApplicationName) {
        this.clientApplicationName = clientApplicationName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public ServiceConfig getService() {
        return service;
    }

    public void setService(ServiceConfig service) {
        this.service = service;
    }
}
