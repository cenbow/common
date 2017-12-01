package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/21.
 * 
 */
public class L003_LongestSubstringWithoutRepeatingCharacters {

    // TODO: 17/7/22 速度太慢,算法有待改进
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s == "") {
            return 0;
        }
        int len = 0;
        char[] chars = new char[256];
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                int index = s.charAt(j);
                if (chars[index] == 0) {
                    chars[index] = 1;
                    if (j == s.length() - 1) {//遍历到最后一位任然没有重复
                        len = Math.max(len, j - i + 1);
                        break;
                    }
                } else {
                    len = Math.max(len, j - i);
                    break;
                }

            }
            chars = new char[256];// TODO: 17/7/22  占空间大
        }
        return len;
    }
}
