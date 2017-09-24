package kelly.leetcode;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by kelly.li on 17/7/23.
 */
public class TestLeetCode {


    @Test
    public void test1() throws Exception {
        URL in = Thread.currentThread().getContextClassLoader().getResource("leetcode.txt");
        Map<String, List<String>> leetCode = new TreeMap<String, List<String>>();
        Map<String, Problem> problems = new HashMap<String, Problem>();
        List<String> lines = FileUtils.readLines(new File(in.getFile()));
        String curCompany = null;
        for (String line : lines) {
            if (line.trim().length() <= 0) {
                continue;
            }
            if (line.startsWith("##")) {
                curCompany = line.replace("##", "").trim();
            } else {
                String[] words = line.split(" ");
                List<String> items = new ArrayList<String>();
                for (String word : words) {
                    if (word.trim().length() > 0) {
                        items.add(word);
                    }
                }
                String id = String.format("%3d", Integer.parseInt(items.get(0)));
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 1; i < items.size() - 2; i++) {
                    stringBuilder.append(items.get(i)).append(" ");
                }
                String title = stringBuilder.toString();
                String acceptance = items.get(items.size() - 2);
                String difficulty = items.get(items.size() - 1);
                problems.put(id, new Problem(id, title, acceptance, difficulty));
                if (leetCode.containsKey(id)) {
                    leetCode.get(id).add(curCompany);
                } else {
                    List<String> companys = new ArrayList<String>();
                    companys.add(curCompany);
                    leetCode.put(id, companys);
                }
            }
        }
        int size = 0;
        for(int frequency = 12; frequency >=1 ;frequency--) {
            System.out.println("\n\n******************\t"+frequency+"\t******************\n");
            for (Map.Entry<String, List<String>> mapEntry : leetCode.entrySet()) {

                if (mapEntry.getValue().size() == frequency) {
                    size++;
                    String id = mapEntry.getKey();
                    Problem problem = problems.get(id);
                    String format = String.format("%s %s\t%s %s\t%s", id, problem.title, problem.acceptance, problem.difficulty, mapEntry.getValue());
                    System.out.println(format);
                }
            }
        }
//        System.out.println(leetCode.size());
//        System.out.println(size);
    }

    class Problem {
        String id;
        String title;
        String acceptance;
        String difficulty;

        public Problem(String id, String title, String acceptance, String difficulty) {
            this.id = id;
            this.title = title;
            this.acceptance = acceptance;
            this.difficulty = difficulty;
        }
    }

}
