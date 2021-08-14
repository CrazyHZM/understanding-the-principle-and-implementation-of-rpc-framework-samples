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


import samples.rpc.framework.cluster.LoadBalance;
import samples.rpc.framework.common.SpringUtil;
import samples.rpc.framework.config.ClientConfig;
import samples.rpc.framework.config.ReferenceConfig;
import samples.rpc.framework.config.ServiceConfig;
import samples.rpc.framework.remoting.Request;
import samples.rpc.framework.remoting.Response;
import samples.rpc.framework.remoting.codec.Decoder;
import samples.rpc.framework.remoting.codec.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author crazyhzm@apache.org
 */
public class Client {

    private Logger logger = Logger.getLogger(Client.class);

    private ReferenceConfig referenceConfig;

    private ChannelFuture channelFuture;

    private ClientHandler clientHandler;

    public Client(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public ServiceConfig connectServer() {
        logger.info("connecting to the server: " +
                referenceConfig.getDirectServerIp() + ":" + referenceConfig.getDirectServerPort());

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Encoder(Request.class));
                        ch.pipeline().addLast(new Decoder(Response.class));

                        clientHandler = new ClientHandler();
                        ch.pipeline().addLast(new RpcReadTimeoutHandler(clientHandler, referenceConfig.getTimeout(), TimeUnit.MILLISECONDS));
                        ch.pipeline().addLast(clientHandler);
                    }
                });

        try {
            if (!StringUtils.isEmpty(referenceConfig.getDirectServerIp())) {
                channelFuture = bootstrap.connect(referenceConfig.getDirectServerIp(), referenceConfig.getDirectServerPort()).sync();
                logger.info("successfully connected");
            } else {
                ClientConfig client = (ClientConfig) SpringUtil.getApplicationContext().getBean("client");
                logger.info("the load balancing strategy is: " + client.getLoadBalance());

                ServiceConfig serviceConfig = LoadBalance.getService(referenceConfig, client.getLoadBalance());

                if (serviceConfig == null) {
                    return null;
                }
                channelFuture = bootstrap.connect(serviceConfig.getIp(), serviceConfig.getPort()).sync();
                logger.info("successfully connected to the server: " + serviceConfig.getIp() + ":" + serviceConfig.getPort());
                return serviceConfig;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Response remoteCall(Request request) throws Throwable {

        // 发送请求
        channelFuture.channel().writeAndFlush(request).sync();
        channelFuture.channel().closeFuture().sync();

        // 接收响应
        Response response = clientHandler.getResponse();
        logger.info("receive a response from the server：" + response.getRequestId());

        if (response.getSuccess()) {
            return response;
        }

        throw response.getError();
    }

}
