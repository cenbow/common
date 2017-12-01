package kelly-leentcode;

import org.junit.Test;

/**
 * Created by kelly-lee on 17/7/18.
 */
public class TestLintCode {


    //{20,1,40,#,#,35,#}, 20
    @Test
    public void test1() {
        TreeNode n20 = new TreeNode(20);
        TreeNode n1 = new TreeNode(1);
        TreeNode n40 = new TreeNode(40);
        TreeNode n35 = new TreeNode(35);
        n20.left = n1;
        n20.right = n40;
        n40.left = n35;
        L087_RemoveNodeInBinarySearchTree l087 = new L087_RemoveNodeInBinarySearchTree();
        TreeNode root = l087.removeNode(n20, 20);

    }


    //{1001,92,#,81,#,75,#,41,#,#,65}, 92
    @Test
    public void test2() {

        TreeNode n1001 = new TreeNode(1001);
        TreeNode n92 = new TreeNode(92);
        TreeNode n81 = new TreeNode(81);
        TreeNode n75 = new TreeNode(75);
        TreeNode n41 = new TreeNode(41);
        TreeNode n65 = new TreeNode(65);
        n1001.left = n92;
        n92.left = n81;
        n81.left = n75;
        n75.left = n41;
        n75.right = n65;

        L087_RemoveNodeInBinarySearchTree l087 = new L087_RemoveNodeInBinarySearchTree();
        TreeNode root = l087.removeNode(n1001, 92);


    }

    //{5,#,10} 5

}
