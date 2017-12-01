package kelly.leetcode;

import java.util.List;

/**
 * Created by kelly-lee on 17/8/30.
 */
public class L120_Triangle {


    public int minimumTotal(List<List<Integer>> triangle) {
        if (triangle == null) {
            return 0;
        }
        int size = triangle.size();
        if (size == 0) {
            return 0;
        }
        List<Integer> row1 = triangle.get(0);
        if(row1.size()==0){
            return 0;
        }
        int[][] result = new int[size][];
        result[0][0] = triangle.get(0).get(0);
        if(size == 1){
            return result[0][0];
        }

        int sum = Integer.MAX_VALUE;


        for (int i = 1; i < size; i++) {
            List<Integer> rows = triangle.get(i);
            for (int j = 0; j < rows.size(); j++) {
                int val = rows.get(i);
                if (j == 0) {
                    result[i][j] = result[i - 1][j] + val;
                }
                if (j == rows.size()-1) {
                    result[i][j] = result[i - 1][j - 1] + val;
                } else {
                    result[i][j] = Math.min(result[i - 1][j - 1], result[i - 1][j]) + val;
                }
                if (i == size - 1) {
                    sum = Math.min(sum, result[i][j]);
                }
            }

        }
        return sum;
    }

    public static void main(String[] args) {

    }

}
