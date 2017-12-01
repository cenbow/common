package kelly.jvm;

import java.nio.ByteBuffer;

/**
 * Created by kelly-lee on 17/8/2.
 */
public class HelloDirectMemoryOutOfMemory {

    public static void main(String[] args) {
        System.out.println("HelloConstantOutOfMemory");
        int count = 0;
        int capacity = 1024 * 1024 * 1024;
        while (true) {
            //intern 重用String对象，以节省内存消耗
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);
            System.out.println("Instance: " + ++count);
        }

    }
}


