package kelly.leetcode;

import java.util.PriorityQueue;

/**
 * Created by kelly-lee on 17/7/29.
 */
public class L215_KthLargestElementInAnArray {

    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>();
        for (int i = 0; i < nums[i]; i++) {
            priorityQueue.add(i);
            if (priorityQueue.size() <= k) {
                priorityQueue.poll();
            }
        }
        return priorityQueue.poll();
    }
}
