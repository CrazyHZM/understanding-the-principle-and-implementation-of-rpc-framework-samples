package samples.serialization.protobuf;




import message.Person;

import java.io.*;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2021/1/17 7:14 下午
 */
public class SerializationTest {

    public static void main(String[] args) {
        // 序列化
        try {
            File file = new File("protobuf.result");
            OutputStream outputStream = new FileOutputStream(file);
            Person.Builder builder = Person.newBuilder();
            builder.setAge(25);
            builder.setName("crazyhzm");
            builder.build().writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 反序列化
        try {
            File file = new File("protobuf.result");
            InputStream inputStream = new FileInputStream(file);
            Person user = Person.parseFrom(inputStream);
            System.out.println(user.getName());
            System.out.println(user.getAge());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
