package kelly.lintcode;

/**
 * Created by kelly.li on 17/7/17.
 */

/**
 * Definition of TreeNode:
 * public class TreeNode {
 * public int val;
 * public TreeNode left, right;
 * public TreeNode(int val) {
 * this.val = val;
 * this.left = this.right = null;
 * }
 * }
 */
public class L085_InsertNodeInABinarySearchTree {

    /**
     * @param root: The root of the binary search tree.
     * @param node: insert this node into the binary search tree
     * @return: The root of the new binary search tree.
     */
    public TreeNode insertNode(TreeNode root, TreeNode node) {
        // write your code here
        if (root == null || node == null) {
            //如果是空树,待插入结点是根结点
            return node;
        }
        TreeNode cur = root; //遍历的当前结点,初始指向根结点
        TreeNode pre = cur; //待插入结点的前继结点
        while (cur != null) {
            pre = cur;
            if (cur.val > node.val) {//当前结点值比待插入结点值大,当前结点向左移动
                cur = cur.left;
            } else {//当前结点值比待插入结点值小,当前结点向右移动
                cur = cur.right;
            }
        }

        if (pre.val >= node.val) {//前继结点值大于等于待插入结点,待插入结点在其左边
            pre.left = node;
        } else {//
            pre.right = node;
        }
        return root;
    }
}
