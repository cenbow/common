package kelly.leetcode;

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

public class L297_SerializeAndDeserializeBinaryTree {

    private static final char NULL = '#';
    private static final char SPLIT = ',';

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder builder = new StringBuilder();
        serialize(root, builder);
        return builder.toString();
    }

    void serialize(TreeNode root, StringBuilder builder) {
        if (root == null) {
            builder.append(NULL).append(SPLIT);
            return;
        }
        builder.append(root.val).append(SPLIT);
        serialize(root.left, builder);
        serialize(root.right, builder);
    }


    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        return deserialize(new DataIter(data));
    }


    TreeNode deserialize(DataIter iter) {
        if (!iter.hasNext()) {
            iter.nextNULL();
            return null;
        }
        Integer val = iter.nextInt();
        if (val == null) {
            return null;
        }
        TreeNode treeNode = new TreeNode(val);
        treeNode.left = deserialize(iter);
        treeNode.right = deserialize(iter);
        return treeNode;
    }

    class DataIter {
        final String data;
        int index;

        DataIter(String data) {
            this.data = data;
        }

        boolean hasNext() {
            return index < data.length() && data.charAt(index) != NULL;
        }

        void nextNULL() {
            index += 2;
        }

        Integer nextInt() {

            int splitIndex = data.indexOf(SPLIT, index);
            if (splitIndex < 0) {
                return null;
            }
            String next = data.substring(index, splitIndex);
            index = ++splitIndex;
            return Integer.parseInt(next);
        }
    }
}
// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.deserialize(codec.serialize(root));