package kelly.leetcode;

import java.util.LinkedList;

/**
 * Created by kelly-lee on 17/7/19.
 *
 */
public class L316_RemoveDuplicateLetters {

    // TODO: 17/7/19   没明白题意,写法不正确
    public String removeDuplicateLetters(String s) {
        char[] chars = new char[26];
        LinkedList<Character> list = new LinkedList<Character>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (chars[c - 'a'] == 0) {
                chars[c - 'a'] = c;
                list.offer(c);
            } else {
                if (list.peek() == c) {
                    list.poll();
                    list.offer(c);
                }
            }

        }

        char[] newChars = new char[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newChars[i] = list.get(i);
        }

        return new String(newChars);
    }
}
