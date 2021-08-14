package samples.serialization.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2021/1/17 7:14 下午
 */
public class SerializationTest {

    public static void main(String[] args) {

        customizeObjectSerializationWithObjectTest();
    }

    private static void originalObjectSerializationTest() {
        Kryo kryo = new Kryo();
        String name = "crazyhzm";
        try {
            Output output = new Output(new FileOutputStream("kryo.result"));
            kryo.writeObject(output, name);
            output.close();
        } catch (FileNotFoundException e) {
        }
        try {
            Input input = new Input(new FileInputStream("kryo.result"));
            String newName = kryo.readObject(input, String.class);
            System.out.println(newName);
            input.close();
        } catch (FileNotFoundException e) {
        }
    }

    private static void customizeObjectSerializationTest() {
        Kryo kryo = new Kryo();
        kryo.register(Person.class);
        Person person = new Person("crazyhzm", 25);
        try {
            Output output = new Output(new FileOutputStream("kryo.result"));
            kryo.writeObject(output, person);
            output.close();
        } catch (FileNotFoundException e) {
        }
        try {
            Input input = new Input(new FileInputStream("kryo.result"));
            Person newPerson = kryo.readObject(input, Person.class);
            System.out.println(newPerson.getName());
            System.out.println(newPerson.getAge());
            input.close();
        } catch (FileNotFoundException e) {
        }
    }

    private static void originalObjectSerializationWithObjectTest() {
        Kryo kryo = new Kryo();
        String name = "crazyhzm";
        // 序列化
        try {
            Output output = new Output(new FileOutputStream("kryo.result"));
            kryo.writeClassAndObject(output, name);
            output.close();
        } catch (FileNotFoundException e) {
        }
        // 反序列化
        try {
            Input input = new Input(new FileInputStream("kryo.result"));
            String newName = (String)kryo.readClassAndObject(input);
            System.out.println(newName);
            input.close();
        } catch (FileNotFoundException e) {
        }
    }

    private static void customizeObjectSerializationWithObjectTest() {
        Kryo kryo = new Kryo();
        kryo.register(Person.class);
        Person person = new Person("crazyhzm", 25);
        try {
            Output output = new Output(new FileOutputStream("kryo.result"));
            kryo.writeClassAndObject(output, person);
            output.close();
        } catch (FileNotFoundException e) {
        }
        try {
            Input input = new Input(new FileInputStream("kryo.result"));
            Person newPerson = (Person) kryo.readClassAndObject(input);
            System.out.println(newPerson.getName());
            System.out.println(newPerson.getAge());
            input.close();
        } catch (FileNotFoundException e) {
        }
    }
}
