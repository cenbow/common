package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/19.
 */
public class L020_ValidParentheses {


    public boolean isValid(String s) {
        char[] stack = new char[s.length()];
        int index = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (index == -1) {
                stack[++index] = c;
                continue;
            }
            char pc = stack[index];
            //()[]{}"
            if ((pc == '(' && c == ')') || (pc == '[' && c == ']') || (pc == '{' && c == '}')) {
                index--;
                continue;
            }
            stack[++index] = c;
        }
        return index == -1;
    }

}
