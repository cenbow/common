package kelly.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly.li on 17/8/16.
 */
public class L046_Permutations {


    public List<List<Integer>> permute(int[] nums) {
        ArrayList<List<Integer>> rst = new ArrayList<List<Integer>>();
        if (nums == null) {
            return rst;
        }

        if (nums.length == 0) {
            rst.add(new ArrayList<Integer>());
            return rst;
        }

        ArrayList<Integer> list = new ArrayList<Integer>();
        helper(rst, list, nums);
        return rst;
    }

    public void helper(ArrayList<List<Integer>> rst, ArrayList<Integer> list, int[] nums) {
        if (list.size() == nums.length) {
            rst.add(new ArrayList<Integer>(list));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (list.contains(nums[i])) {
                continue;
            }
            list.add(nums[i]);
            System.out.println(list + "   add:" + nums[i]);
            System.out.println("----helper begin:" + i);
            helper(rst, list, nums);
            System.out.println("-----helper end:" + i);
            list.remove(list.size() - 1);
            System.out.println(list + "   remove:" + nums[i]);
        }

    }

}
