package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/17.
 * 二叉搜索树中序遍历找后继
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
public class L285_InorderSuccessorInBST {

    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        if (root == null || p == null) {
            return null;
        }
        TreeNode cur = root;
        TreeNode suc = null;
        while (cur != null) {
            if (cur.val > p.val) {
                cur = cur.left;
                suc = cur;
            } else {
                cur = cur.right;
            }
        }
        return suc;
    }
}
