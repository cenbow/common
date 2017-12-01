package kelly.leetcode;

import java.util.ArrayList;
import java.util.List;

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
public class L103_BinaryTreeZigzagLevelOrderTraversal {

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> ll = new ArrayList<List<Integer>>();
        zigzagLevelOrder(ll, root, 1);
        return ll;
    }

    void zigzagLevelOrder(List<List<Integer>> ll, TreeNode root, int level) {
        if (root == null) {
            return;
        }
        if (level > ll.size()) {
            ll.add(new ArrayList<Integer>());
        }
        if (level % 2 == 0) {
            ll.get(level - 1).add(0, root.val);
        } else {
            ll.get(level - 1).add(root.val);
        }
        zigzagLevelOrder(ll, root.left, level + 1);
        zigzagLevelOrder(ll, root.right, level + 1);
    }

}
