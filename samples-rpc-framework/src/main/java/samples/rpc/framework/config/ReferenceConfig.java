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

import samples.rpc.framework.common.ReferenceUtil;
import samples.rpc.framework.common.SpringUtil;
import samples.rpc.framework.proxy.JavassistProxyFactory;
import samples.rpc.framework.registry.ServiceChangeListener;
import samples.rpc.framework.registry.ZookeeperClient;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author crazyhzm@apache.org
 */
public class ReferenceConfig implements InitializingBean, ApplicationContextAware, FactoryBean, Serializable {
    private transient Logger logger = Logger.getLogger(ReferenceConfig.class);

    private transient ApplicationContext applicationContext;

    private String id;

    private String name;

    private String directServerIp;

    private int directServerPort;

    private String version;

    private long timeout;

    private long refCount;

    private transient List<ServiceConfig> services;

    private String ip;

    /**
     * 获取spring上下文对象
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 在spring实例化全部的bean之后执行
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (!applicationContext.containsBean("application")) {
            logger.info("没有配置application，无法获取引用");
            return;
        }
        if (!applicationContext.containsBean("client")) {
            logger.info("没有配置client，无法获取引用");
            return;
        }

        // 如果是点对点服务，不需要配置注册中心
        if (!StringUtils.isEmpty(directServerIp)) {
            logger.info("点对点服务，" + directServerIp + ":" + directServerPort);
            return;
        }

        if (!applicationContext.containsBean("register")) {
            logger.info("没有配置register，无法获取引用");
            return;
        }

        init();
    }

    /**
     * 初始化
     *
     * @throws Exception
     */
    public void init() throws Exception {
        // 发布客户端引用到注册中心
        registerReference();

        // 获取引用
        getReferences();

        // 缓存引用
        ReferenceUtil.put(this);

        // 订阅服务变化
        subscribeServiceChange();
    }

    /**
     * 订阅服务变化
     */
    private void subscribeServiceChange() {
        RegisterConfig register = (RegisterConfig) SpringUtil.getApplicationContext().getBean("register");
        String path = "/samples/" + name + "/provider";
        logger.info("Start subscription service: [" + path + "]");
        // 订阅子目录变化
        ZookeeperClient.getInstance(register.getIp(), register.getPort()).subscribeChildChange(path, new ServiceChangeListener(name));
    }

    /**
     * 发布客户端引用到注册中心
     *
     * @throws Exception
     */
    private void registerReference() throws Exception {
        RegisterConfig register = (RegisterConfig) SpringUtil.getApplicationContext().getBean("register");
        ip = InetAddress.getLocalHost().getHostAddress();

        // zookeeper
        String basePath = "/samples/" + this.getName() + "/consumer";
        String path = basePath + "/" + ip;

        ZookeeperClient client = ZookeeperClient.getInstance(register.getIp(), register.getPort());

        // 应用（路径）永久保存
        client.createPath(basePath);

        // 服务(数据)不永久保存，当与zookeeper断开连接20s左右自动删除
        client.saveNode(path, this);
        logger.info("客户端引用发布成功:[" + path + "]");
    }

    /**
     * 获取引用
     *
     * @throws Exception
     */

    public void getReferences() throws Exception {
        String path = "/samples/" + name + "/provider";
        logger.info("正在获取引用服务:[" + path + "]");
        RegisterConfig register = (RegisterConfig) SpringUtil.getApplicationContext().getBean("register");
        services = new ArrayList<>();
        ZookeeperClient zookeeperClient = ZookeeperClient.getInstance(register.getIp(), register.getPort());
        List<String> nodes = zookeeperClient.getChildNodes(path);

        for (String node : nodes) {
            ServiceConfig service = (ServiceConfig) zookeeperClient.getNode(path + "/" + node);
            // 版本为空，则可以匹配任意版本，都在必须匹配一致的版本
            if (!StringUtils.isEmpty(version) && !version.equals(service.getVersion())) {
                continue;
            }
            services.add(service);
        }

        logger.info("引用服务获取完成[" + path + "]:" + services);
    }

    @Override
    public Object getObject() throws Exception {
        Class<?> clazz = getObjectType();
        // 动态代理，获取远程服务实例
        return JavassistProxyFactory.PROXY_FACTORY.getProxy(clazz, this);
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            logger.error("没有对应的服务", e);
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public String toString() {
        return "Refrence{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", directServerIp='" + directServerIp + '\'' +
                ", directServerPort=" + directServerPort +
                ", version=" + version +
                ", timeout=" + timeout +
                ", refCount=" + refCount +
                ", services=" + services +
                ", ip=" + ip +
                '}';
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getDirectServerIp() {
        return directServerIp;
    }

    public void setDirectServerIp(String directServerIp) {
        this.directServerIp = directServerIp;
    }

    public int getDirectServerPort() {
        return directServerPort;
    }

    public void setDirectServerPort(int directServerPort) {
        this.directServerPort = directServerPort;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    public List<ServiceConfig> getServices() {
        return services;
    }

    public void setServices(List<ServiceConfig> services) {
        this.services = services;
    }

    public long getRefCount() {
        return refCount;
    }

    public void setRefCount(long refCount) {
        this.refCount = refCount;
    }
}
