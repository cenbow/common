package kelly.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly-lee on 17/7/17.
 */
public class L257_BinaryTreePaths {

    private static final String SPLIT = "->";

    public List<String> binaryTreePaths(TreeNode root) {
        List<String> paths = new ArrayList<String>();
        binaryTreePaths(root, paths, "");
        return paths;
    }

    public void binaryTreePaths(TreeNode root, List<String> paths, String path) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            path += path + root.val;
            paths.add(path.toString());
            return;
        } else {
            path += root.val + SPLIT;
        }
        binaryTreePaths(root.left, paths, path);
        binaryTreePaths(root.right, paths, path);

    }


}
