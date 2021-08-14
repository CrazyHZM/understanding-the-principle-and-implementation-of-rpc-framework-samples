package samples.serialization.xstream;

import com.thoughtworks.xstream.XStream;

import java.math.BigDecimal;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2021/1/17 7:14 下午
 */
public class SerializationTest {

    public static void main(String[] args) {
        XStream xstream = new XStream();
        xstream.alias("person", Person.class);
        Person p = new Person("crazyhzm", 20,new BigDecimal(100));
        String xml = xstream.toXML(p);
        System.out.println(xml);

        Person np = (Person)xstream.fromXML(xml);

    }

}
