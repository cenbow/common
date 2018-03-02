package kelly.monitor.alert;

import kelly.monitor.common.AlertType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kelly-lee on 2018/2/12.
 */
public class TestAlertType {


    @Test
    public void test1() {
        AlertType alertType = new AlertType(AlertType.SMS_MAIL_WECHAT);
        Assert.assertTrue(alertType.isMail());
        Assert.assertTrue(alertType.isSMS());
        Assert.assertTrue(alertType.isWechat());
        Assert.assertFalse(alertType.isMq());
    }

    @Test
    public void test2() {
        AlertType alertType = new AlertType(AlertType.SMS);
//        alertType.enableSMS();
        Assert.assertFalse(alertType.isMail());
        Assert.assertTrue(alertType.isSMS());
        Assert.assertFalse(alertType.isWechat());
        Assert.assertFalse(alertType.isMq());
    }

    @Test
    public void test3() {
        AlertType alertType = new AlertType(AlertType.WECHAT);
//        alertType.enableWechat();
        Assert.assertFalse(alertType.isMail());
        Assert.assertFalse(alertType.isSMS());
        Assert.assertTrue(alertType.isWechat());
        Assert.assertFalse(alertType.isMq());
    }

    @Test
    public void test4() {
        AlertType alertType = new AlertType(AlertType.MAIL);
//        alertType.enableMail();
        Assert.assertTrue(alertType.isMail());
        Assert.assertFalse(alertType.isSMS());
        Assert.assertFalse(alertType.isWechat());
        Assert.assertFalse(alertType.isMq());
    }

    @Test
    public void test5() {
        AlertType alertType = new AlertType(AlertType.MQ);
//        alertType.enableMail();
        Assert.assertFalse(alertType.isMail());
        Assert.assertFalse(alertType.isSMS());
        Assert.assertFalse(alertType.isWechat());
        Assert.assertTrue(alertType.isMq());
    }

    @Test
    public void test6() {
        AlertType alertType = new AlertType();
        alertType.enableMail();
        alertType.enableSMS();
        alertType.enableMq();
        Assert.assertTrue(alertType.isMail());
        Assert.assertTrue(alertType.isSMS());
        Assert.assertFalse(alertType.isWechat());
        Assert.assertTrue(alertType.isMq());

    }
}
