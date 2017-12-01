package kelly.amazon;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by kelly-lee on 17/8/31.
 */
public class A08_Maze {

    static final int[][] dir = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static int checkMaze(int[][] maze) {
        if (maze == null || maze.length == 0 || maze[0][0] == 0) {
            return 0;
        }
        if (maze[0][0] == 9) {
            return 1;
        }
        int m = maze.length;
        int n = maze[0].length;
        Queue<int[]> queue = new LinkedList<int[]>();
        queue.offer(new int[]{0, 0});
        maze[0][0] = 0;
        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            for (int i = 0; i < dir.length; i++) {
                int x = p[0] + dir[i][0];
                int y = p[1] + dir[i][1];
                if (x > 0 && x < m && y > 0 && y < m) {
                    if (maze[x][y] == 9) {
                        return 1;
                    }
                    if (maze[x][y] == 1) {
                        queue.offer(new int[]{x, y});
                        maze[x][y] = 0;
                    }
                }
            }
        }
        return 0;
    }
}
