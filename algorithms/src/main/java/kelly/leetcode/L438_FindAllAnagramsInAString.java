package kelly.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly-lee on 17/8/27.
 */
public class L438_FindAllAnagramsInAString {

//Input: s: "cbaebabacd" p: "abc"
//Output: [0, 6]


    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> list = new ArrayList<Integer>();
        int[] arr = new int[26];
        for (int i = 0; i < p.length(); i++) {
            arr[p.charAt(i) - 'a']++;
        }
        int len = p.length();
        int left = 0;
        int right = 0;
        while (right < s.length()) {

        }
        return list;
    }
}
