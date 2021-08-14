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

import samples.rpc.framework.config.ReferenceConfig;
import samples.rpc.framework.config.RpcContext;
import samples.rpc.framework.config.ServiceConfig;
import samples.rpc.framework.remoting.Request;
import samples.rpc.framework.remoting.Response;
import samples.rpc.framework.remoting.transport.Client;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author crazyhzm@apache.org
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private Logger logger = Logger.getLogger(InvokerInvocationHandler.class);

    private ReferenceConfig referenceConfig;

    public InvokerInvocationHandler(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(method.getName(), method.getParameterTypes(), args);
    }

    public Object invoke(String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 同步调用
        return remoteCall(referenceConfig, methodName, argTypes, args);
    }

    private Object remoteCall(ReferenceConfig refrence, String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 准备请求参数
        Request request = new Request();
        // 请求id
        request.setRequestId(RpcContext.getUuid().get());
        request.setClientApplicationName(RpcContext.getApplicationName());
        request.setClientIp(RpcContext.getLocalIp());
        // 必要参数
        request.setClassName(referenceConfig.getName());
        request.setMethodName(methodName);
        request.setTypes(getTypes(argTypes));
        request.setArgs(args);
        Response response;
        try {
            Client client = new Client(refrence);
            ServiceConfig service = client.connectServer();
            request.setService(service);
            response = client.remoteCall(request);
            return response.getResult();
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
    }


    /**
     * 获取方法的参数类型
     *
     * @param methodTypes
     * @return
     */
    private String[] getTypes(Class<?>[] methodTypes) {
        String[] types = new String[methodTypes.length];
        for (int i = 0; i < methodTypes.length; i++) {
            types[i] = methodTypes[i].getName();
        }
        return types;
    }
}
