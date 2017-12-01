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
public class L113_PathSumII {

    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> paths = new ArrayList<List<Integer>>();
        pathSum(root, sum, paths, new ArrayList<Integer>());
        return paths;
    }

    void pathSum(TreeNode root, int sum, List<List<Integer>> paths, List<Integer> path) {
        if (root == null) {
            return;
        }
        sum -= root.val;
        if (root.left == null && root.right == null) {
            if (sum == 0) {
                path.add(root.val);
                paths.add(new ArrayList<Integer>(path));
                path.remove(path.size() - 1);
            }
            return;
        }
        path.add(root.val);
        pathSum(root.left, sum, paths, path);
        pathSum(root.right, sum, paths, path);
        path.remove(path.size() - 1);
    }

}


