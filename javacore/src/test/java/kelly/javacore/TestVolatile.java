package kelly.javacore;

import org.junit.Test;

/**
 * Created by kelly.li on 17/8/11.
 * <p>
 * 内存可见性问题,当多个线程操作共享数据时,彼此不可见
 * volatile 关键字:当多个线程进行操作共享数据时,可以保证内存中的数据可见。相当于synchronized 是一种较为轻量级的同步策略。
 * <p>
 * 注意:
 * 1.volatile 不具备"互斥性"
 * 2.volatile 不能保证变量的"原子性"
 */
public class TestVolatile {

    @Test
    public void test1() {
        MyThread t = new MyThread();
        t.start();
        while (true) { //无机会读取主存数据
//            synchronized (t) {
                if (t.isFlag()) {
                    System.out.println("read thread : " + t.isFlag());
                    break;
                }
//            }
        }
    }

    class MyThread extends Thread {

        private volatile  boolean flag = false;

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flag = true;
            System.out.println("write thread : " + flag);
        }

        public boolean isFlag() {
            return flag;
        }

    }
}
