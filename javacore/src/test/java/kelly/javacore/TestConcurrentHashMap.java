package kelly.javacore;

/**
 * Created by kelly.li on 17/8/11.
 * <p>
 * HashMap 线程不安全
 * Hashtable 内部采用独占锁，线程安全，但效率低
 * ConcurrentHashMap同步容器类是java5 新增的一个线程安全的哈希表，效率介于HashMap和Hashtable之间。内部采用“锁分段”机制。
 * <p>
 * <p>
 * java.util.concurrent 包还提供了设计用于多线程上下文中的Collection实现：
 * <p>
 * 当期望许多线程访问一个给定 collection 时，
 * ConcurrentHashMap 通常优于同步的 HashMap，
 * ConcurrentSkipListMap 通常优于同步的 TreeMap
 * ConcurrentSkipListSet通常优于同步的 TreeSet.
 * <p>
 * 当期望的读数和遍历远远大于列表的更新数时，
 * CopyOnWriteArrayList 优于同步的 ArrayList。因为每次添加时都会进行复制，开销非常的大，并发迭代操作多时 ，选择。
 */
public class TestConcurrentHashMap {
}
