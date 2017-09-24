package kelly.leetcode;

/**
 * Created by kelly.li on 17/8/26.
 */
public class L074_SearchA2DMatrix {

    //思路2：从右上角元素开始遍历，每次遍历中若与target相等则返回true；若小于则行向下移动；若大于则列向左移动。时间复杂度m+n

    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) {
            return false;
        }
        int m = matrix.length;
        int n = matrix[0].length;
        int left = 0;
        int right = m * n - 1;
        while (left <= right) {
            int middle = left + (right - left) / 2;
            int x = middle / n;
            int y = middle % n;
            if (matrix[x][y] == target) {
                return true;
            }
            if (matrix[x][y] < target) left = middle + 1;
            else right = middle - 1;
        }
        return false;
    }

}
