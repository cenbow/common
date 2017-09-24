package kelly.javacore;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kelly.li on 17/8/17.
 */
public class TestLog {

    //日志中输出异常信息的前几行和后几行
    @Test
    public void test1() throws IOException {
        Object obj = new Object();
        URL in = Thread.currentThread().getContextClassLoader().getResource("1.txt");
        List<String> lines = FileUtils.readLines(new File(in.getFile()));
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int start = 0;
        int end = 0;
        boolean flag = false;
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.endsWith("Exception") || line.endsWith("Error")) {
                start = index;
                flag = true;
            } else if (flag && !line.trim().startsWith("at")) {
                end = index;
                flag = false;
                map.put(start, end);
            }

        }

        int num = 3;
        start = 0;
        end = 0;
        for (int index = 0; index < lines.size(); index++) {
            if (map.containsKey(index)) {
                start = index;
                end = map.get(start);
                String line = lines.get(index);
                System.out.println(line);
            } else if ((index > start && index < start + num + 1) || (index < end && index > end - num - 1)) {
                String line = lines.get(index);
                System.out.println(line);
            }
        }
    }

    //输出斐波那契数列

    @Test
    public void test2() {
        for (int i = 1; i <= 20; i++) {
            System.out.print(getFibonacci(i) + "\t");
            if (i % 5 == 0) {
                System.out.println();
            }
        }
    }


    public int getFibonacci(int i) {
        if (i == 1 || i == 2) {
            return 1;
        } else {
            return getFibonacci(i - 1) + getFibonacci(i - 2);
        }
    }


    @Test
    public void testBubbleSort() {
        int[] arr = new int[]{49, 38, 65, 97, 76, 13, 27, 49, 78, 34, 12, 64, 5, 4, 62, 99, 98, 54, 56, 17, 18, 23, 34, 15, 35, 25, 53, 51};
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));
    }


}
