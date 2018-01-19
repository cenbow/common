package kelly.monitor.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Created by kelly-lee on 2017/10/17.
 */
public class Constants {

    public static final String TOMCAT_TH = "tomcat_th";
    public static final String SESSION_TH = "session_th";
    public static final String APPID = "appid";
    public static final String MYSQL_TH = "mysql_th";
    public static final String MYSQL_51 = "mysql_51";
    public static final String MYSQL_55 = "mysql_55";
    public static final String MYSQL_IVR = "mysql_IVR";
    public static final String API_PATH = "/direct/getSysStatus.action";
    public static final Integer API_STATUS_OK = 0;
    public static final Integer API_STATUS_ERROR = 1;

    public static final Splitter SPLITTER_OR = Splitter.on("|").trimResults();
    public static final Splitter SPLITTER_EQUAL = Splitter.on("=").omitEmptyStrings().trimResults();
    public static final Joiner JOINER = Joiner.on("|").skipNulls();
}
