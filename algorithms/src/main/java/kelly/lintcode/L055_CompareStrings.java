package kelly.lintcode;

/**
 * Created by kelly.li on 17/7/22.
 */
public class L055_CompareStrings {

    /**
     * @param A : A string includes Upper Case letters
     * @param B : A string includes Upper Case letter
     * @return :  if string A contains all of the characters in B return true else return false
     */
    public boolean compareStrings(String A, String B) {
        // write your code here
        if (A == null && B == null) {
            return true;
        }
        if (A == null || B == null) {
            return false;
        }
        int[] count = new int[26];

        boolean flag = true;
        for (int i = 0; i < A.length(); i++) {
            int index = A.charAt(i) - 'A';
            count[index]++;
        }

        for (int i = 0; i < B.length(); i++) {
            int index = B.charAt(i) - 'A';
            count[index]--;
            if (count[index] < 0) {
                flag = false;
                break;
            }
        }

        return flag;
    }


}
