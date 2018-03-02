package kelly.monitor.common;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/2/12.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AlertType {

    public static final int NONE = 0;//0000
    public static final int SMS = 1;//0001
    public static final int MAIL = 2;//0010
    public static final int WECHAT = 4;//0100
    public static final int SMS_MAIL_WECHAT = 7;//0111
    public static final int MQ = 8;//1000

    public static final AlertType DEFAULT = new AlertType(SMS_MAIL_WECHAT);

    @NonNull
    private int value;

    public String toDescription() {
        List<String> alertTypes = Lists.newArrayList();
        if (isSMS()) {
            alertTypes.add("短信");
        }
        if (isMail()) {
            alertTypes.add("邮件");
        }
        if (isWechat()) {
            alertTypes.add("微信");
        }
        return alertTypes.stream().collect(Collectors.joining("、"));
    }


    public boolean isWechat() {
        return isType(WECHAT);
    }

    public boolean isSMS() {
        System.out.println(value);
        return isType(SMS);
    }

    public boolean isMail() {
        return isType(MAIL);
    }

    public boolean isMq() {
        return isType(MQ);
    }

    public void enableWechat() {
        enableType(WECHAT);
    }

    public void enableSMS() {
        enableType(SMS);
    }

    public void enableMail() {
        enableType(MAIL);
    }

    public void enableMq() {
        enableType(MQ);
    }

    private void enableType(int type) {
        this.value |= type;
    }

    private boolean isType(int type) {
        return (value & type) > NONE;
    }


}
