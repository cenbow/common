package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/16.
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
public class L107_BinaryTreeLevelOrderTraversalII {

    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> ll = new ArrayList<List<Integer>>();
        levelOrderBottom(ll, root, 1);
        List<List<Integer>> rl = new ArrayList<List<Integer>>(ll.size());
        for (int i = ll.size() - 1; i >= 0; i--) {
            rl.add(ll.get(i));
        }
        return rl;
    }

    public void levelOrderBottom(List<List<Integer>> ll, TreeNode root, int level) {
        if (root == null) {
            return;
        }
        if (level > ll.size()) {
            ll.add(new ArrayList<Integer>());
        }
        ll.get(level - 1).add(root.val);
        levelOrderBottom(ll, root.left, level + 1);
        levelOrderBottom(ll, root.right, level + 1);
    }

}


