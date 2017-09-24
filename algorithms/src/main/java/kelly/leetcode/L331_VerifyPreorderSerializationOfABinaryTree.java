package kelly.leetcode;

import java.util.Stack;

/**
 * Created by kelly.li on 17/7/19.
 */
public class L331_VerifyPreorderSerializationOfABinaryTree {


    public boolean isValidSerialization(String preorder) {
        if (preorder.equals("#")) {
            return true;
        }
        Stack<String> stack = new Stack<String>();
        String[] nodes = preorder.split(",");
        for (int i = 0; i < nodes.length; i++) {
            stack.push(nodes[i]);
            while (stack.size() >= 3 && stack.peek().equals("#")) {
                String s1 = stack.pop();
                if (stack.peek().equals("#")) {
                    String s2 = stack.pop();
                    if (!stack.peek().equals("#")) {
                        stack.pop();
                        stack.push("#");
                        continue;
                    }
                    stack.push(s2);
                }
                stack.push(s1);
                break;
            }
        }
        return stack.size() == 1 && stack.peek().equals("#");
    }


//    public boolean isValidSerialization(String preorder) {
//        int degree = 1;
//        String[] tokens = preorder.split(",");
//        for (String token: tokens) {
//            if (--degree < 0) return false;
//            if (!token.equals("#"))
//                degree += 2;
//        }
//        return degree == 0;
//    }
}
