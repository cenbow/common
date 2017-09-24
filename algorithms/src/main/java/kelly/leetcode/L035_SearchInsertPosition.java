package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/18.
 */
public class L035_SearchInsertPosition {

    public int searchInsert(int[] nums, int target) {
        int left = 0;
        int right = nums.length + 1;
        int ans = 0;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (guess(mid, nums, target)) {
                ans = mid;
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return ans;
    }

    boolean guess(int mid, int[] nums, int target) {
        if (mid >= nums.length) {
            return true;
        }
        return nums[mid] >= target;
    }
}
