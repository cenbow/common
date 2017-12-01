package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/16.
 */

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 * int val;
 * TreeNode left;
 * TreeNode right;
 * TreeNode(int x) { val = x; }
 * }
 */
public class L111_MinimumDepthOfBinaryTree {

    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        if (root.left == null) {
            return minDepth(root.right) + 1;
        }
        if (root.right == null) {
            return minDepth(root.left) + 1;
        }
        int lmd = minDepth(root.left);
        int rmd = minDepth(root.right);
        return Math.min(lmd, rmd) + 1;
    }

}


