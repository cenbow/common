package kelly.monitor.alert;

import org.junit.Test;

import static kelly.monitor.alert.BuildData.buildTimeExpression;

/**
 * Created by kelly-lee on 2018/2/9.
 */
public class TestTimeExpression {


    @Test
    public void test1() {
        System.out.println(buildTimeExpression().toDescrption());
    }


}
