package kelly.leetcode;

import org.junit.Test;

import java.util.List;

/**
 * Created by kelly.li on 17/8/16.
 */
public class TestDFS {

    @Test
    public void test1() {
        L046_Permutations l046 = new L046_Permutations();
        List<List<Integer>> result = l046.permute(new int[]{1,2,3});
        System.out.println(result);
    }
}
