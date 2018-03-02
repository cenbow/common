package kelly.monitor.alert;

import kelly.monitor.alert.checker.CountChecker;
import org.junit.Test;

/**
 * Created by kelly.li on 2018/2/19.
 */
public class TestCountChecker {

    @Test
    public void test1() {
        CountChecker.Result result = CountChecker.check("key", false, 3);
        System.out.println(result + " " + CountChecker.getCount("key"));
        for (int i = 0; i < 5; i++) {
            result = CountChecker.check("key", true, 3);
            System.out.println(result + " " + CountChecker.getCount("key"));
        }
        result = CountChecker.check("key", false, 3);
        System.out.println(result + " " + CountChecker.getCount("key"));
        result = CountChecker.check("key", false, 3);
        System.out.println(result + " " + CountChecker.getCount("key"));
        result = CountChecker.check("key", false, 3);
        System.out.println(result + " " + CountChecker.getCount("key"));
        for (int i = 0; i < 5; i++) {
            result = CountChecker.check("key", true, 3);
            System.out.println(result + " " + CountChecker.getCount("key"));
        }

    }
}
