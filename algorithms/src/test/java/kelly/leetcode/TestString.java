package kelly.leetcode;

import org.junit.Test;

/**
 * Created by kelly-lee on 17/7/22.
 */
public class TestString {


    @Test
    public void test003() {
        L003_LongestSubstringWithoutRepeatingCharacters l003 = new L003_LongestSubstringWithoutRepeatingCharacters();
        int maxLen = l003.lengthOfLongestSubstring("abcabcbb");
        System.out.println(maxLen);
    }


    @Test
    public void test001(){
        String str = "leetcode";
        System.out.println(str.substring(0,5));
    }



}
