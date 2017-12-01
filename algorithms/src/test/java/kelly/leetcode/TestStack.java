package kelly.leetcode;

import org.junit.Test;

/**
 * Created by kelly-lee on 17/7/19.
 */
public class TestStack {

    //["MinStack","push","push","push","push","getMin","pop","getMin","pop","getMin","pop","getMin"]
    //[[],[2],[0],[3],[0],[],[],[],[],[],[],[]]

    @Test
    public void test155() {
        L155_MinStack stack = new L155_MinStack();
        stack.push(512);
        stack.push(-1024);
        stack.push(-1024);
        stack.push(512);
        stack.pop();
        System.out.println(stack.getMin());
        stack.pop();
        System.out.println(stack.getMin());

        stack.pop();
        System.out.println(stack.getMin());
    }


//["MyStack","push","push","push","top"]
//[[],[1],[2],[3],[]]

    @Test
    public void test225() {
        L225_ImplementStackUsingQueues stack = new L225_ImplementStackUsingQueues();
        stack.push(1);
        stack.push(2);
        System.out.println(stack.top());
        stack.push(3);
        System.out.println(stack.top());
    }


    //Given "bcabc" Return "abc"
    //Given "cbacdcbc" Return "acdb"

    @Test
    public void test316(){
        L316_RemoveDuplicateLetters l316 = new L316_RemoveDuplicateLetters();
        System.out.println(l316.removeDuplicateLetters("cbacdcbc"));
    }

    @Test
    //"9,3,4,#,#,1,#,#,2,#,6,#,#"
    public void test331(){
        L331_VerifyPreorderSerializationOfABinaryTree l331 = new L331_VerifyPreorderSerializationOfABinaryTree();
        boolean result = l331.isValidSerialization("9,3,4,#,#,1,#,#,2,#,6,#,#");
        System.out.println(result);
    }
}


