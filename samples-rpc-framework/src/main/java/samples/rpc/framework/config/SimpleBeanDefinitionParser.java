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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @author crazyhzm@apache.org
 */
public class SimpleBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    private Class<?> pojoClass;

    SimpleBeanDefinitionParser(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return pojoClass;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        NamedNodeMap attributes = element.getAttributes();
        for (int x = 0; x < attributes.getLength(); x++) {
            Attr attribute = (Attr) attributes.item(x);
            if (isEligibleAttribute(attribute, parserContext)) {
                String propertyName = extractPropertyName(attribute.getLocalName());
                Assert.state(StringUtils.hasText(propertyName), "Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
                builder.addPropertyValue(underLineToCamel(propertyName), attribute.getValue());
            }
        }
        postProcess(builder, element);
    }

    /**
     * 下划线字符串转驼峰字符串
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    private String underLineToCamel(String str) {
        String[] arr = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                sb.append(arr[i]);
            } else {
                sb.append(firstToUpperCase(arr[i]));
            }
        }

        return sb.toString();
    }

    /**
     * 字符串首字母变大写
     *
     * @param str 字符串
     * @return 首字母变为大写之后的字符串
     */
    private String firstToUpperCase(String str) {
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
