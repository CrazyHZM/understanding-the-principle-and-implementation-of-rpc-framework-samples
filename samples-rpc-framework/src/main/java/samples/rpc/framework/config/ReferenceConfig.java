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
     * ??????spring???????????????
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * ???spring??????????????????bean????????????
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (!applicationContext.containsBean("application")) {
            logger.info("????????????application?????????????????????");
            return;
        }
        if (!applicationContext.containsBean("client")) {
            logger.info("????????????client?????????????????????");
            return;
        }

        // ??????????????????????????????????????????????????????
        if (!StringUtils.isEmpty(directServerIp)) {
            logger.info("??????????????????" + directServerIp + ":" + directServerPort);
            return;
        }

        if (!applicationContext.containsBean("register")) {
            logger.info("????????????register?????????????????????");
            return;
        }

        init();
    }

    /**
     * ?????????
     *
     * @throws Exception
     */
    public void init() throws Exception {
        // ????????????????????????????????????
        registerReference();

        // ????????????
        getReferences();

        // ????????????
        ReferenceUtil.put(this);

        // ??????????????????
        subscribeServiceChange();
    }

    /**
     * ??????????????????
     */
    private void subscribeServiceChange() {
        RegisterConfig register = (RegisterConfig) SpringUtil.getApplicationContext().getBean("register");
        String path = "/samples/" + name + "/provider";
        logger.info("Start subscription service: [" + path + "]");
        // ?????????????????????
        ZookeeperClient.getInstance(register.getIp(), register.getPort()).subscribeChildChange(path, new ServiceChangeListener(name));
    }

    /**
     * ????????????????????????????????????
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

        // ??????????????????????????????
        client.createPath(basePath);

        // ??????(??????)????????????????????????zookeeper????????????20s??????????????????
        client.saveNode(path, this);
        logger.info("???????????????????????????:[" + path + "]");
    }

    /**
     * ????????????
     *
     * @throws Exception
     */

    public void getReferences() throws Exception {
        String path = "/samples/" + name + "/provider";
        logger.info("????????????????????????:[" + path + "]");
        RegisterConfig register = (RegisterConfig) SpringUtil.getApplicationContext().getBean("register");
        services = new ArrayList<>();
        ZookeeperClient zookeeperClient = ZookeeperClient.getInstance(register.getIp(), register.getPort());
        List<String> nodes = zookeeperClient.getChildNodes(path);

        for (String node : nodes) {
            ServiceConfig service = (ServiceConfig) zookeeperClient.getNode(path + "/" + node);
            // ??????????????????????????????????????????????????????????????????????????????
            if (!StringUtils.isEmpty(version) && !version.equals(service.getVersion())) {
                continue;
            }
            services.add(service);
        }

        logger.info("????????????????????????[" + path + "]:" + services);
    }

    @Override
    public Object getObject() throws Exception {
        Class<?> clazz = getObjectType();
        // ???????????????????????????????????????
        return JavassistProxyFactory.PROXY_FACTORY.getProxy(clazz, this);
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            logger.error("?????????????????????", e);
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
