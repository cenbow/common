package kelly.amazon;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by kelly-lee on 17/9/3.
 */
public class A13_SlidingWindowMaximum {

    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k < 0 || k > nums.length) {
            return new int[0];
        }
        Deque<Integer> deque = new LinkedList<Integer>();//存储下标
        int size = nums.length;
        int resIndex = 0;
        int[] result = new int[size - k + 1];
        for (int i = 0; i < nums.length; i++) {
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                //在滑动窗口之前的下标要清除
                deque.pollFirst();
            }
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                //滑动窗口里比当前值小的要清除
                deque.pollLast();
            }
            //下标放在队列之后
            deque.offerLast(i);
            if (i >= k - 1) {
                result[resIndex++] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        A13_SlidingWindowMaximum slidingWindowMaximum = new A13_SlidingWindowMaximum();
        int[] result = slidingWindowMaximum.maxSlidingWindow(new int[]{1}, 1);
        System.out.println(result);
    }
}
