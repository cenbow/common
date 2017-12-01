package kelly.leetcode;

import java.util.*;

/**
 * Created by kelly-lee on 17/7/29.
 */
public class L380_InsertDeleteGetRandomO1 {

    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    List<Integer> list = new ArrayList<Integer>();
    Random random = new Random();

    /**
     * Initialize your data structure here.
     */
    public L380_InsertDeleteGetRandomO1() {

    }

    /**
     * Inserts a value to the set. Returns true if the set did not already contain the specified element.
     */
    public boolean insert(int val) {
        if (!map.containsKey(val)) {
            return false;
        }
        map.put(val, list.size() - 1);
        list.add(val);
        return true;
    }

    /**
     * Removes a value from the set. Returns true if the set contained the specified element.
     */
    public boolean remove(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        int loc = map.get(val);
        if (loc < list.size() - 1) {
            int last = list.get(list.size() - 1);
            list.set(loc, last);
            map.put(last, loc);
        }
        map.remove(val);
        list.remove(list.size() - 1);
        return true;
    }

    /**
     * Get a random element from the set.
     */
    public int getRandom() {
        if (list.isEmpty()) {
            return -1;
        }
        return list.get(random.nextInt(list.size()));
    }


}


/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
