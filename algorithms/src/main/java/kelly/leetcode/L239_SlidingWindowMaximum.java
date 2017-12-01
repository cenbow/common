package kelly.leetcode;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by kelly-lee on 17/9/3.
 */
public class L239_SlidingWindowMaximum {

    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k < 0 || k > nums.length) {
            return new int[0];
        }
        Deque<Integer> deque = new LinkedList<Integer>();
        int size = nums.length;
        int resIndex = 0;
        int[] result = new int[size - k + 1];
        for (int i = 0; i < nums.length; i++) {
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            while(!deque.isEmpty() && nums[deque.peekLast()] < nums[i]){
                deque.pollLast();
            }
            deque.offerLast(i);
            if(i >= k - 1 ){
                result[resIndex++] = nums[deque.peekFirst()];
            }
        }
        return result;
    }
}
