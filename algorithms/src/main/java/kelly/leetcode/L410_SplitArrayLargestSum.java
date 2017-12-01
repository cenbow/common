package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/18.
 */
public class L410_SplitArrayLargestSum {


    public int splitArray(int[] nums, int m) {
        long left = 0;
        long right = 1;
        for (int i = 0; i < nums.length; i++) {
            right += nums[i];
        }
        long ans = 0;
        while (left < right) {
            long mid = left + (right - left) / 2;
            if (guess(mid, nums, m)) {
                System.out.println(mid);
                ans = mid;
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return (int)ans;
    }


    boolean guess(long mid, int[] nums, long m) {
        long sum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (sum + nums[i] > mid) {
                m--;
                if (nums[i] > mid) {
                    return false;
                }
                sum = nums[i];

            } else {
                sum += nums[i];
            }
        }
        return m >= 1;
    }


}
