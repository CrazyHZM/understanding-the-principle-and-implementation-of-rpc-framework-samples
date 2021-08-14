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

package samples.rpc.framework.registry;

import samples.rpc.framework.serialization.Serializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crazyhzm@apache.org
 */
public class ZookeeperClient {

    private Logger logger = Logger.getLogger(ZookeeperClient.class);

    /**
     * 单例
     */
    private static ZookeeperClient instance;

    private CuratorFramework zkClient;

    private ZookeeperClient(String ip, String port) {
        logger.info("start to connect to the zk server：[" + ip + ":" + port + "]");
        zkClient = CuratorFrameworkFactory.newClient(ip + ":" + port, new RetryNTimes(10, 5000));
        zkClient.start();
        logger.info("successfully connected to the zk server：[" + ip + ":" + port + "]");
    }

    public static ZookeeperClient getInstance(String ip, String port) {
        if (null == instance) {
            instance = new ZookeeperClient(ip, port);
        }
        return instance;
    }


    /**
     * 判断路径是否存在
     *
     * @param path
     */
    private boolean exists(String path) {
        boolean result = false;
        try {
            result = zkClient.checkExists().forPath(path) != null;
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }

    /**
     * 创建永久节点
     *
     * @param path 路径
     */
    public void createPath(String path) {
        if (!exists(path)) {
            String[] paths = path.substring(1).split("/");
            String temp = "";
            for (String dir : paths) {
                temp += "/" + dir;
                if (!exists(temp)) {
                    try {
                        zkClient.create().withMode(CreateMode.PERSISTENT).forPath(temp);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
    }

    /**
     * 创建临时节点
     *
     * @param path 数据保存路径
     * @param data 数据
     */
    public void saveNode(String path, Object data) {
        try {
            if (exists(path)) {
                zkClient.setData().forPath(path, Serializer.serialize(data));

            } else {
                String[] paths = path.substring(1).split("/");
                String temp = "";
                for (String dir : paths) {
                    temp += "/" + dir;
                    if (!exists(temp)) {
                        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(temp);
                    }
                }
                zkClient.setData().forPath(path, Serializer.serialize(data));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 获取子节点
     *
     * @param path
     * @return
     */
    public List<String> getChildNodes(String path) throws Exception{
        if (!exists(path)) {
            return new ArrayList<>();
        }
        return zkClient.getChildren().forPath(path);
    }

    /**
     * 获取节点
     *
     * @param path
     * @return
     */
    public Object getNode(String path) {
        if (!exists(path)) {
            return null;
        }
        try {
            return Serializer.deserialize(zkClient.getData().forPath(path));

        }catch (Exception e){
            logger.error(e);
            return null;
        }
    }

    /**
     * 订阅服务变化
     *
     * @param path
     * @param listener
     */
    public void subscribeChildChange(String path, CuratorListener listener) {
        zkClient.getCuratorListenable().addListener(listener);
    }
}
