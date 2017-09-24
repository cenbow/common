package kelly.leetcode;

/**
 * Created by kelly.li on 17/8/15.
 */
public class L200_NumberOfIslands {


    public int numIslands(char[][] grid) {

        if (grid == null || grid.length == 0) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    numIslands(grid, i, j);
                    count++;
                }
            }
        }
        return count;
    }


    void numIslands(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length) {
            return;
        }
        if (grid[i][j] != '1') {
            return;
        }
        grid[i][j] = '0';
        numIslands(grid, i - 1, j);
        numIslands(grid, i + 1, j);
        numIslands(grid, i, j - 1);
        numIslands(grid, i, j + 1);
    }
}
