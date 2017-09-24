package kelly.leetcode;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        URL in = Thread.currentThread().getContextClassLoader().getResource("1.txt");
        List<String> lines = FileUtils.readLines(new File(in.getFile()));
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int start = 0;
        int end = 0;
        boolean flag = false;
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.endsWith("Exception") || line.endsWith("Error")) {
                System.out.println("aa");
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
            } else if ((index > start  && index < start + num + 1) || (index < end && index > end - num - 1)) {
                String line = lines.get(index);
                System.out.println(line);
            }
        }
    }
}
