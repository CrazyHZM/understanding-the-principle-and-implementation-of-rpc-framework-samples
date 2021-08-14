package samples.serialization.xstream;

import java.math.BigDecimal;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2019-09-25 07:40
 */
public class Person {
    private String name;
    private Integer age;
    private BigDecimal salary;

    public Person(String name, Integer age, BigDecimal salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
