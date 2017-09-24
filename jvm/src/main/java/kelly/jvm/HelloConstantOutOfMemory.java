package kelly.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly.li on 17/8/2.
 */
//-verbose:gc -Xms10M -Xmx10M -XX:MaxDirectMemorySize=5M -Xss160k -XX:+PrintGCDetails
public class HelloConstantOutOfMemory {
    public static void main(String[] args) {
        System.out.println("HelloConstantOutOfMemory");
        List<String> list = new ArrayList<String>();
        int count = 0;
        while (true) {
            //intern 重用String对象，以节省内存消耗
            list.add(String.valueOf(count).intern());
            System.out.println("Instance: " + ++count);
        }

    }

}
