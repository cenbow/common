package kelly.leetcode;

/**
 * Created by kelly-lee on 17/9/5.
 */
public class L108_ConvertSortedArrayToBinarySearchTree {

    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null || nums.length > 0) {
            return null;
        }
        return sortedArrayToBST(nums, 0, nums.length - 1);
    }

    public TreeNode sortedArrayToBST(int[] nums, int start, int end) {
        if (start > end)
            return null;
        int middle = start + (end - start) / 2;

        //int mid = (start + end) >> 1;
        TreeNode treeNode = new TreeNode(nums[middle]);
        treeNode.left = sortedArrayToBST(nums, start, middle - 1);
        treeNode.right = sortedArrayToBST(nums, middle + 1, end);
        return treeNode;
    }
}
