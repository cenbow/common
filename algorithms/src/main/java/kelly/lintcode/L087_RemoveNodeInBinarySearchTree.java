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
public class L087_RemoveNodeInBinarySearchTree {


    /**
     * @param root:  The root of the binary search tree.
     * @param value: Remove the node with given value.
     * @return: The root of the binary search tree after removal.
     */
    public TreeNode removeNode(TreeNode root, int value) {
        // write your code here
        if (root == null) {
            return null;
        }
        //查找待删除的结点和它的父结点
        TreeNode dummy = new TreeNode(root.val);
        dummy.left = root;
        TreeNode parent = dummy;
        TreeNode cur = root;
        TreeNode del = null;
        while (cur != null) {
            if (cur.val == value) {
                del = cur;
                break;
            }
            parent = cur;
            if (cur.val > value) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }

        if (del == null) {//如果找不到node则返回root
            return root;
        }
        if (del.right == null) {//如果node的右子树为空，直接将node的左子树赋给node的parent节点
            if (parent.val >= del.val) {//等于处理删除结点是顶点的情况
                parent.left = del.left;
            } else {
                parent.right = del.left;
            }
        } else { //如果node的右子树不为空，则需要找一个node的后继（即在右子树中找一个值最小的节点）替换node；
            TreeNode suc = del;
            TreeNode psuc = suc;
            cur = del.right;
            while (cur != null) {
                psuc = suc;
                suc = cur;
                if (cur.val > del.val) {
                    cur = cur.left;
                } else {
                    cur = cur.right;
                }

            }
            del.val = suc.val;
            if (suc.right != null) {
                if (psuc.val >= suc.right.val) {
                    psuc.left = suc.right;
                } else {
                    psuc.right = suc.right;
                }
            } else {
                psuc.left = null;
                psuc.right = null;
            }
        }
        if (parent == dummy) {
            return parent.left;
        }
        return root;
    }


}
