package kelly.javacore;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kelly-lee on 17/8/11.
 * <p>
 * 一、i++ 的原子性问题: i++ 的操作实际上分为三个步骤"读-改-写"
 * int i = 10;
 * i = i++;//10
 * <p>
 * int temp = i;
 * i = i + 1;
 * i = temp;
 * <p>
 * 二、原子变量: jdk1.5 后 java.util.concurrent.atomic 包下提供了常用的原子变量:
 * 1. volatile 保证内存可见性
 * 2. cas (Compare-And-Swap)算法保证数据的原子性
 * cas 算法是硬件对于并发操作共享数据的支持
 * cas 包含了三个操作数:
 * 内存值 V
 * 预估值 A
 * 更新值 B
 * 当且仅当 V == A 时,V = B ,否则,将不做任何操作
 */
public class TestAtomic {

    @Test
    public void test1() throws IOException {
        MyThread t = new MyThread();
        for (int i = 0; i < 10; i++) {
            new Thread(t).start();
        }
        System.in.read();
    }


    class MyThread implements Runnable {

        // private  int serialNumber = 0;

        private AtomicInteger serialNumber = new AtomicInteger();

        @Override
        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println("write thread : " + serialNumber++ );
            System.out.println("write thread : " + serialNumber.getAndIncrement());
        }

        public int getSerialNumber() {
//            return serialNumber;
            return serialNumber.get();
        }

    }
}
