package kelly.monitor.alert;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kelly-lee on 2018/2/9.
 */
public class TestTimeExpression {


    @Test
    public void test1() {
        TimeExpression timeExpression = new TimeExpression("00:00-12:59,14:00-18:00", "#P98>50 OR #MIN_1>1500");
        Assert.assertTrue(timeExpression.matchTimeRange());

    }


}
