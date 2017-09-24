package kelly.leetcode;

import java.util.Stack;

/**
 * Created by kelly.li on 17/7/17.
 */

/**
 * Definition for binary tree
 * public class TreeNode {
 * int val;
 * TreeNode left;
 * TreeNode right;
 * TreeNode(int x) { val = x; }
 * }
 */

public class L173_BinarySearchTreeIterator {

    Stack<TreeNode> stack = new Stack<TreeNode>();

    public L173_BinarySearchTreeIterator(TreeNode root) {
        if (root == null) {
            return;
        }
        pushLeft(root);
    }

    /**
     * @return whether we have a next smallest number
     */
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    /**
     * @return the next smallest number
     */
    public int next() {
        TreeNode treeNode = stack.pop();
        int val = treeNode.val;
        if (treeNode.right != null) {
            pushLeft(treeNode.right);
        }
        return val;
    }

    void pushLeft(TreeNode root) {
        TreeNode cur = root;
        while (cur != null) {
            stack.push(cur);
            cur = cur.left;
        }
    }

}

/**
 * Your BSTIterator will be called like this:
 * BSTIterator i = new BSTIterator(root);
 * while (i.hasNext()) v[f()] = i.next();
 */
