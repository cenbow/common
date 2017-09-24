package kelly.leetcode;

import java.util.List;

/**
 * Created by kelly.li on 17/7/24.
 */
public class L139_WordBreak {


    public boolean wordBreak(String s, List<String> wordDict) {
        if (s == null || s.length() == 0) {
            return true;
        }
        boolean[] ans = new boolean[s.length() + 1];
        ans[0] = true;
        for (int i = 0; i < s.length(); i++) {
            StringBuilder builder = new StringBuilder(s.substring(0, i + 1));
            for (int j = 0; j <= i; i++) {
                if (ans[j] && wordDict.contains(builder.toString())) {
                    ans[i + 1] = true;
                    break;
                }
                builder.deleteCharAt(0);
            }
        }
        return ans[s.length()];

    }
}
