package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/16.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 * int val;
 * TreeNode left;
 * TreeNode right;
 * TreeNode(int x) { val = x; }
 * }
 */
public class L102_BinaryTreeLevelOrderTraversal {

    public List<List<Integer>> levelOrder(TreeNode root) {

        List<List<Integer>> ll = new ArrayList<List<Integer>>();
        levelOrder(ll, root, 1);
        return ll;
    }

    public void levelOrder(List<List<Integer>> ll, TreeNode root, int level) {
        if (root == null) {
            return;
        }
        if (level > ll.size()) {
            ll.add(new ArrayList<Integer>());
        }
        ll.get(level - 1).add(root.val);
        levelOrder(ll, root.left, level + 1);
        levelOrder(ll, root.right, level + 1);

    }
}
