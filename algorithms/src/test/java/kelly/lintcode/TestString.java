package kelly-leentcode;

import org.junit.Test;

/**
 * Created by kelly-lee on 17/7/22.
 */
public class TestString {


    @Test
    public void test158() {
        L158_TwoStringsAreAnagrams l158 = new L158_TwoStringsAreAnagrams();
        //Given s = "abcd", t = "dcab", return true.
        //Given s = "ab", t = "ab", return true.
        //Given s = "ab", t = "ac", return false.
        boolean flag = l158.anagram("ab", "ab");
        System.out.println(flag);
    }

    @Test
    public void test055() {
    //For A = "ABCD", B = "ACD", return true.
    //For A = "ABCD", B = "AABC", return false.
        //"AAAAAAAAAAAABBBBBCDD", "ABCDD"
        L055_CompareStrings l055 = new L055_CompareStrings();
        boolean flag = l055.compareStrings("AAAAAAAAAAAABBBBBCDD", "ABCDD");
        System.out.println(flag);
    }



    @Test
    public void test013() {
        //If source = "source" and target = "target", return -1.
        //If source = "abcdabcdefg" and target = "bcd", return 1.
        L013_StrStr l013 = new L013_StrStr();
        int result = l013.strStr("source","rced");
        System.out.println(result);
    }
}
