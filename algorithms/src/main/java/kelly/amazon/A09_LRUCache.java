package kelly.amazon;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kelly.li on 17/7/23.
 */
public class A09_LRUCache {

    int capacity;
    Node head;
    Node tail;
    Map<Integer, Node> map;

    public A09_LRUCache(int capacity) {
        this.capacity = capacity;
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
        map = new HashMap<Integer, Node>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        Node node = map.get(key);
        //
        node.prev.next = node.next;
        node.next.prev = node.prev;
        //
        node.prev = tail.prev;
        tail.prev = node;
        node.prev.next = node;
        node.next= tail;


        return node.value;
    }

    public void put(int key, int value) {
        if (get(key) != -1) {
            map.get(key).value = value;
            return;
        }
        if (map.size() == capacity) {
            map.remove(head.next.key);
            head.next = head.next.next;
            head.next.prev = head;
        }
        Node node = new Node(key, value);
        map.put(key, node);
        node.prev = tail.prev;
        tail.prev = node;
        node.prev.next = node;
        node.next= tail;
    }


    class Node {
        Node prev;
        Node next;
        int key;
        int value;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */