package kelly.zookeeper;

import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixreversed.ConcurrentReversedRadixTree;
import kelly.zookeeper.observer.EventResolver;
import kelly.zookeeper.observer.ZkObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kelly.li on 17/11/18.
 */
public class TestObserver {
    String connectString = "192.168.99.100:2181";
    String namespace = "test";
    String username = "admin";
    String password = "123";
    ZkClient curZkClient;
    ZkClient zkClient;
    ZkClient zkClientWithNs;
    ZkClient zkClientWithAuth;

    @Before
    public void before() {
        zkClient = new DefaultZkClient(connectString);
        zkClientWithNs = new DefaultZkClient(connectString, namespace);
        zkClientWithAuth = new DefaultZkClient(connectString, namespace, username, password);
        curZkClient = zkClientWithAuth;
    }

    @Test
    public void test1() throws Exception {
        ZkObserver zkObserver = curZkClient.addObserver("/", new EventResolver());
        zkObserver.start();
        System.in.read();
    }


    @Test
    public void test2() {
        ConcurrentRadixTree<String> concurrentRadixTree = new ConcurrentRadixTree<String>(new DefaultCharArrayNodeFactory());
        //mq subject
        concurrentRadixTree.putIfAbsent("mq.test.a", "/a");
        concurrentRadixTree.putIfAbsent("mq.test.a.a", "/a/aa");
        concurrentRadixTree.putIfAbsent("mq.test.a.b", "/a/aa/aaa");
        concurrentRadixTree.putIfAbsent("mq.test.b", "/a/aa/aaa");
        System.out.println(concurrentRadixTree.getValueForExactKey("mq.test"));
        System.out.println(concurrentRadixTree.getValueForExactKey("mq.test"));
        System.out.println(concurrentRadixTree.getValueForExactKey("c"));
        //mq prefix
        Iterable<KeyValuePair<String>> iterable = concurrentRadixTree.getKeyValuePairsForKeysStartingWith("mq.test");
        for (KeyValuePair<String> keyValuePair : iterable) {
            System.out.println(keyValuePair.getKey() + "=" + keyValuePair.getValue());
        }
    }

    @Test
    public void test3() {
        ConcurrentInvertedRadixTree<String> concurrentRadixTree = new ConcurrentInvertedRadixTree<String>(new DefaultCharArrayNodeFactory());
        //mq prefix
        concurrentRadixTree.putIfAbsent("mq.test.a", "/a");
        concurrentRadixTree.putIfAbsent("mq.test.a.a", "/a/aa");
        concurrentRadixTree.putIfAbsent("mq.test.a.b", "/a/aa/aaa");
        concurrentRadixTree.putIfAbsent("mq.test.b", "/a/aa/aaa");
        //mq subject
        for(CharSequence key : concurrentRadixTree.getKeysContainedIn("mq.test.a.a")){
            System.out.println(key);
        }
        for(KeyValuePair<String> keyValuePair : concurrentRadixTree.getKeyValuePairsForKeysContainedIn("mq.test.a.a")){
            System.out.println(keyValuePair.getKey() + "=" + keyValuePair.getValue());
        }

    }

    @Test
    public void test4() {
        ConcurrentReversedRadixTree<String> concurrentRadixTree = new ConcurrentReversedRadixTree<String>(new DefaultCharArrayNodeFactory());
        concurrentRadixTree.putIfAbsent("mq.test.a", "/a");
        concurrentRadixTree.putIfAbsent("mq.test.a.a", "/a/aa");
        concurrentRadixTree.putIfAbsent("mq.test.a.b", "/a/aa/aaa");
        concurrentRadixTree.putIfAbsent("mq.test.b", "/a/aa/aaa");
        for(CharSequence key : concurrentRadixTree.getKeysEndingWith("a")){
            System.out.println(key);
        }
        for(KeyValuePair<String> keyValuePair : concurrentRadixTree.getKeyValuePairsForKeysEndingWith("a")){
            System.out.println(keyValuePair.getKey() + "=" + keyValuePair.getValue());
        }

    }

    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
        zkClientWithAuth.close();
    }


}
