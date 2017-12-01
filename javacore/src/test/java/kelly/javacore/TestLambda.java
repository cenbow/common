package kelly.javacore;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by kelly-lee on 17/8/9.
 * <p>
 * Lambda 表达式的基础语法:Java8中引入了一个新的操作符 "->" 该操作符称为箭头操作符或Lambda操作符,箭头操作符将Lambda表达式拆分成两部分:
 * 左侧:Lambda 表达式的参数列表
 * 右侧:Lambda 表达式中所需执行的功能,即 Lambda 体
 * Lambda 表达式需要"函数式接口"的支持:接口中只有一个抽象方法,可以使用注解@FunctionalInterface修饰,可以检查是否是函数式接口
 * <p>
 */
public class TestLambda {


    @Test
    public void test1() {
        Comparator<Integer> comparator = new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1.intValue(), o2.intValue());
            }
        };
        TreeSet<Integer> treeSet = new TreeSet<Integer>(comparator);
    }


    @Test
    public void test2() throws Exception {
        Comparator<Integer> comparator = (x, y) -> Integer.compare(x, y);
        throw new Exception();
      //  PrintWriter printer = new PrintWriter();

    }


    @Test
    public void test3() {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(new Employee(1, "a", 18, 19.9f));
        employees.add(new Employee(2, "b", 28, 29.9f));
        employees.add(new Employee(3, "c", 38, 39.9f));
        employees.add(new Employee(4, "d", 48, 49.9f));
        List<Employee> filteredEmployees = filter(employees, (e) -> e.age > 18);
        employees.forEach(System.out::println);
        for (Employee employee : filteredEmployees) {
            System.out.println(employee.age);
        }
    }

    public List<Employee> filter(List<Employee> employees, EmployeeFilter employeeFilter) {
        List<Employee> filteredEmployees = new ArrayList<Employee>();
        for (Employee employee : employees) {
            if (employeeFilter.filter(employee)) {
                filteredEmployees.add(employee);
            }
        }
        return filteredEmployees;
    }

    @Test
    public void test4() {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(new Employee(1, "a", 18, 19.9f));
        employees.add(new Employee(2, "b", 28, 29.9f));
        employees.add(new Employee(3, "c", 38, 39.9f));
        employees.add(new Employee(4, "d", 48, 49.9f));
        employees.stream().filter(employee -> employee.salary > 20).limit(2).forEach(System.out::println);
        employees.stream().map(Employee::getName).forEach(System.out::println);
    }

    // 语法格式一:无参数无返回值 () ->
    // 语法格式二:有一个参数,并且无返回值 (x) -> System.out.println(x)
    // 语法格式三:若只有一个参数,小括号可以省略不写 x -> System.out.println(x)
    // 语法格式四:有两个以上的参数,有返回值,并且Lambda体中有多条语句 (x,y) -> {多行代码}
    // 语法格式五:有两个以上的参数,有返回值,并且Lambda体中有一条语句(return和大括号都可以省略) (x,y) -> nteger.compare(x, y)
    // 语法格式六:Lambda表达式的参数列表的数据类型可以省略不写,因为JVM编译器通过上下文推断出,数据类型,即"类型推断"
    // 左右遇一括号省,左侧推断类型省
    @Test
    public void test5() {
        Runnable r = () -> System.out.println("hello lambda");
        Consumer c = (x) -> System.out.println(x);
        c = x -> System.out.println(x);
        Comparator<Integer> comparator = (x, y) -> {
            System.out.println(x + "," + y);
            return Integer.compare(x, y);
        };
        comparator = (x, y) -> Integer.compare(x, y);

    }

    //类型推断
    public void test6() {
        String[] strs = {"aaa", "bbb", "ccc"};
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();

    }

    //java 内置的四大核心函数式接口
    //Consumer<T>消费, 有参数无返回值
    //Supplier<T>供给, 无参数有返回值
    //Function<T,R>函数, 有参数有返回值
    //Predicate<T>判定 有参数,返回值类型是boolean
    public void test7() {

    }

    class Employee {

        int id;
        String name;
        int age;
        float salary;

        public Employee(int id, String name, int age, float salary) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.salary = salary;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", salary=" + salary +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public float getSalary() {
            return salary;
        }

        public void setSalary(float salary) {
            this.salary = salary;
        }
    }

    @FunctionalInterface
    interface EmployeeFilter {
        public boolean filter(Employee employee);
    }

}
