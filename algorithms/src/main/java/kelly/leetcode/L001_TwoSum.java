package kelly.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kelly-lee on 17/7/21.
 */
public class L001_TwoSum {

//nums = {2,7,5,11}  target = 9

    public int[] twoSum(int[] nums, int target) {
        //在map中保存和减去当前数的结果,和这个数的下标。
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if (map.containsKey(num)) {
                return new int[]{map.get(num) + 1, i + 1};
            }
            map.put(target - nums[i], i);
        }
        return null;
    }


}
