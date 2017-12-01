package kelly.leetcode;

import org.junit.Test;

import java.util.List;

/**
 * Created by kelly-lee on 17/7/16.
 */
public class TestBinaryTree {


    @Test
    public void test() {
        L297_SerializeAndDeserializeBinaryTree l297 = new L297_SerializeAndDeserializeBinaryTree();
        TreeNode treeNode = l297.deserialize("1 2 3 ");

        String data = l297.serialize(treeNode);
        System.out.println(data);


        L257_BinaryTreePaths l257 = new L257_BinaryTreePaths();
        List<String> paths = l257.binaryTreePaths(treeNode);
        System.out.println(paths);

    }


}
