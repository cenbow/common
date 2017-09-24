package kelly.leetcode;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by kelly.li on 17/7/18.
 */
public class TestBinarySearch {

    @Test
    public void test035() {
        L035_SearchInsertPosition l035 = new L035_SearchInsertPosition();
        int[] nums = new int[]{1};
        int target = 2;
        int result = l035.searchInsert(nums, target);
        System.out.println(result);
    }

    @Test
    public void test069() {
        // 2147483647
        L069_SqrtX l069 = new L069_SqrtX();
        int result = l069.mySqrt(2147483647);
        System.out.println(result);
    }

    @Test
    public void test167() {
        L167_TwoSumII l167 = new L167_TwoSumII();
        int[] nums = new int[]{2, 7, 11, 15};
        int target = 9;
        int[] result = l167.twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }

    @Test
    public void test410() {
        L410_SplitArrayLargestSum l410 = new L410_SplitArrayLargestSum();
        int[] nums = new int[]{1, 2147483646};
        int m = 1;
        int result = l410.splitArray(nums, m);
        System.out.println(result);
    }
}
