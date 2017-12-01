package kelly.lintcode;

/**
 * Created by kelly-lee on 17/7/22.
 */
public class L013_StrStr {

    /**
     * Returns a index to the first occurrence of target in source,
     * or -1  if target is not part of source.
     *
     * @param source string to be scanned.
     * @param target string containing the sequence of characters to match.
     */
    public int strStr(String source, String target) {
        // write your code here
        if (source == null && target == null) {
            return -1;
        }
        if (target == "") {
            return 0;
        }
        if (source == null || target == null) {
            return -1;
        }
        int ans = -1;
        loop:
        for (int i = 0; i < source.length(); i++) {
            int cur = 0;
            for (int j = i; j < source.length(); j++) {
                char c = source.charAt(j);
                if (cur < target.length() && c == target.charAt(cur)) {
                    if (cur == 0) {
                        ans = j;
                    }
                    cur++;
                    if (cur == target.length()) {
                        break loop;
                    }
                } else if (cur > 0) {
                    ans = -1;
                    break;
                }
            }
        }
        return ans;
    }


}
