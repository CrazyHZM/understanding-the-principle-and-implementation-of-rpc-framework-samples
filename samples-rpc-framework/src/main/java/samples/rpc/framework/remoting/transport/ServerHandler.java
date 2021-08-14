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

package samples.rpc.framework.remoting.transport;

import samples.rpc.framework.common.SpringUtil;
import samples.rpc.framework.common.TypeParseUtil;
import samples.rpc.framework.config.ServiceConfig;
import samples.rpc.framework.remoting.Request;
import samples.rpc.framework.remoting.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author crazyhzm@apache.org
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = Logger.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Response response = new Response();
        Request request = (Request) msg;
        response.setRequestId(request.getRequestId());
        try {
            logger.info("the server receives the message:" + request.getRequestId());

            // 获取本地暴露的所有服务
            Map<String, ServiceConfig> serviceMap = SpringUtil.getApplicationContext().getBeansOfType(ServiceConfig.class);
            ServiceConfig service = null;

            // 匹配客户端请求的服务
            for (String key : serviceMap.keySet()) {
                if (serviceMap.get(key).getName().equals(request.getClassName())) {
                    service = serviceMap.get(key);
                    break;
                }
            }
            if (service == null) {
                throw new RuntimeException("no service found: " + request.getClassName());
            }
            // 获取服务的实现类
            Object serviceImpl = SpringUtil.getApplicationContext().getBean(service.getRef());
            if (serviceImpl == null) {
                throw new RuntimeException("no available service found: " + request.getClassName());
            }

            // 转换参数和参数类型
            Map<String, Object> map = TypeParseUtil.parseTypeString2Class(request.getTypes(), request.getArgs());
            Class<?>[] classTypes = (Class<?>[]) map.get("classTypes");
            Object[] args = (Object[]) map.get("args");

            // 通过反射调用方法获取返回值
            Object result = serviceImpl.getClass().getMethod(request.getMethodName(), classTypes).invoke(serviceImpl, args);
            response.setResult(result);
            response.setSuccess(true);
        } catch (Throwable e) {
            logger.error("the server failed to process the request.", e);
            response.setSuccess(false);
            response.setError(e);
        }
        ctx.write(response);
        ctx.flush();
    }
}
