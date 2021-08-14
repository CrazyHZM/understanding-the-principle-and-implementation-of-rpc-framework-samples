package samples.serialization.fastjson;


import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2021/1/17 7:14 下午
 */
public class SerializationTest {

    public static void main(String[] args) {
        Person person = new Person("crazyhzm", 20, new BigDecimal("1234567.890123"));
        String jsonString = JSON.toJSONString(person);
        System.out.println(jsonString);
        Person user = JSON.parseObject(jsonString, Person.class);


    }

}
