package kelly.lintcode;

/**
 * Created by kelly-lee on 17/7/22.
 */
public class L158_TwoStringsAreAnagrams {

    /**
     * @param s: The first string
     * @param t: The second string
     * @return true or false
     */
    public boolean anagram(String s, String t) {
        if (s == null && t == null) {
            return true;
        }
        if (s == null || t == null) {
            return false;
        }
        int[] count = new int[256];

        boolean flag = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            count[c]++;
        }

        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (--count[c] < 0) {
                flag = false;
                break;
            }
        }

        return flag;
    }


}
