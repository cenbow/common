package kelly.monitor.alert.notify;

import kelly.monitor.common.AlertInfo;

/**
 * Created by kelly-lee on 2018/2/13.
 */
public interface AlertNotify {

    public void notify(AlertInfo alertInfo);
}
