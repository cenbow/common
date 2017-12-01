package kelly.javacore;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kelly-lee on 17/7/31.
 */
public class MyThread extends Thread {

    public void state() {

        Thread.State s1 = State.NEW;//新建状态、初始化状态
        Thread.State s2 = State.RUNNABLE;//可运行状态、就绪状态
        Thread.State s3 = State.BLOCKED;//阻塞状态、被中断运行
        Thread.State s4 = State.WAITING;//等待状态
        Thread.State s5 = State.TIMED_WAITING;//定时等待状态
        Thread.State s6 = State.TERMINATED;//死亡状态、终止状态


        Thread thread = new Thread();//NEW
        thread.start();//RUNNABLE
        //线程正在等待其它的线程释放同步锁,
        // 已经进入了某个同步块或同步方法，在运行的过程中它调用了某个对象继承自java.lang.Object的wait()方法，正在等待重新返回这个同步块或同步方法
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //当前线程调用了一下三个方法，正在等待另外一个线程执行某个操作
        try {

            this.wait();//WAITING
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //一个线程调用了另一个线程的join()
            this.join();//WAITING
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.park();//WAITING


        //有一个最大等待时间，即使等待的条件仍然没有满足，只要到了这个时间它就会自动醒来。
        try {

            this.wait(5000);//WAITING
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //一个线程调用了另一个线程的join()
            this.join(5000);//WAITING
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.parkNanos(5000);
        LockSupport.parkUntil(System.currentTimeMillis() + 5000);


        //TERMINATED（死亡状态、终止状态）：线程完成执行后的状态。线程执行完run()方法中的全部代码，从该方法中退出，进入TERMINATED状态。还有一种情况是run()在运行过程中抛出了一个异常，而这个异常没有被程序捕获，导致这个线程异常终止进入TERMINATED状态。
    }


   // ReentrantLock这个类还提供了2种竞争锁的机制：公平锁和非公平锁。这2种机制的意思从字面上也能了解个大概：即对于多线程来说，公平锁会依赖线程进来的顺序，后进来的线程后获得锁。而非公平锁的意思就是后进来的锁也可以和前边等待锁的线程同时竞争锁资源。对于效率来讲，当然是非公平锁效率更高，因为公平锁还要判断是不是线程队列的第一个才会让线程获得锁。
    public void test1() {
        //不会让线程释放它所持有的同步锁；而且在这期间它也不会阻碍其它线程的运行
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            //dosomething
        } finally {
            lock.unlock();
        }
    }


}
