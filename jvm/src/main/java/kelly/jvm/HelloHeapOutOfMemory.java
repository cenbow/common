package kelly.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly.li on 17/8/2.
 */


//-verbose:gc -Xms10M -Xmx10M -XX:MaxDirectMemorySize=5M -Xss160k -XX:+PrintGCDetails
class Person{}
public class HelloHeapOutOfMemory {

    public static void main(String[] args) {
        System.out.println("HellpHeapOutOfMemory");
        List<Person> list = new ArrayList<Person>();
        int count = 0;
        while (true) {
            list.add(new Person());
      //      System.out.println("Instance: " + ++count);
        }

    }
}
