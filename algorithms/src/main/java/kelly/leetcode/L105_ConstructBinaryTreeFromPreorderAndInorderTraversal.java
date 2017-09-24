package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/16.
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
public class L105_ConstructBinaryTreeFromPreorderAndInorderTraversal {


    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0 || inorder == null || inorder.length == 0) {
            return null;
        }
        return buildTree(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1);
    }

    public TreeNode buildTree(int[] preorder, int pre1, int pre2, int[] inorder, int in1, int in2) {
        int precur = pre1;
        int root = preorder[precur];
        TreeNode treeNode = new TreeNode(root);
        if (pre1 == pre2 && in1 == in2) {
            return treeNode;
        }
        int incur = -1;
        if (inorder[in1] == root) {
            incur = in1;
        } else if (inorder[in2] == root) {
            incur = in2;
        } else {
            for (int i = in1; i <= in2; i++) {
                if (inorder[i] == root) {
                    incur = i;
                    break;
                }
            }
        }
        if (incur > in1) {
            treeNode.left = buildTree(preorder, precur + 1, precur + incur - in1, inorder, in1, incur - 1);

        }
        if (incur < in2) {
            treeNode.right = buildTree(preorder, precur + incur - in1 + 1, precur + in2 - in1 - 1, inorder, incur + 1, in2);
        }
        return treeNode;
    }

}


