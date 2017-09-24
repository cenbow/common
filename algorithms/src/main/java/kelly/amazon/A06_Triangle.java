package kelly.amazon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly.li on 17/8/30.
 * leetcode 120
 * <p>
 * [
 * [2],
 * [3,4],
 * [6,5,7],
 * [4,1,8,3]
 * ]
 * <p>
 * 一个N*N的矩阵，第一行1个元素，第二行2个元素。。第N行N个元素，可以构造成一个类似等边三角形，示例请见图。
 * 每个节点可以和它上层两个相邻的结点，以及下层两个相邻的结点连接起来，定义一条路径是从最上层的结点到最下层结点的一条通路，
 * 定义路径的权值为路径上各个结点的权值之和。求最小的路径，以及路径上的结点列表。
 */
public class A06_Triangle {


    public int minimumTotal(List<List<Integer>> triangle) {
        if (triangle == null) {
            return 0;
        }
        int size = triangle.size();
        if (size == 0) {
            return 0;
        }
        List<Integer> row1 = triangle.get(0);
        if (row1.size() == 0) {
            return 0;
        }
        int[][] result = new int[size][size];
        result[0][0] = triangle.get(0).get(0);
        if (size == 1) {
            return result[0][0];
        }

        int sum = Integer.MAX_VALUE;


        for (int i = 1; i < size; i++) {
            List<Integer> rows = triangle.get(i);
            for (int j = 0; j < rows.size(); j++) {
                int val = rows.get(j);//get j
                if (j == 0) {
                    result[i][j] = result[i - 1][j] + val;
                } else if (j == rows.size() - 1) {//else if
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
        List<List<Integer>> list = new ArrayList<List<Integer>>();
        List row1 = new ArrayList();
        row1.add(1);
        list.add(row1);

        List row2 = new ArrayList();
        row2.add(2);
        row2.add(3);
        list.add(row2);

        A06_Triangle triangle = new A06_Triangle();
        int sum = triangle.minimumTotal(list);
        System.out.println(sum);

    }

}
