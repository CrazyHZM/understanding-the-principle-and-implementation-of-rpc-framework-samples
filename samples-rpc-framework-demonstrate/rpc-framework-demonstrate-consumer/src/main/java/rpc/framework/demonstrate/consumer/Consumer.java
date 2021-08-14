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

package rpc.framework.demonstrate.consumer;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import rpc.framework.demonstrate.api.DemoService;
import samples.rpc.framework.common.SpringUtil;

/**
 * @Description:
 * @Author: crazyhzm
 * @Date: Created in 2019-12-19 15:03
 */
public class Consumer {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/consumer.xml");
        context.start();
        DemoService demoService = SpringUtil.getApplicationContext().getBean("demoService", DemoService.class);
        String hello = demoService.hello("world");
        System.out.println("result: " + hello);
    }
}
