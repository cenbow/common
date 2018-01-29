package kelly.monitor.util;

import java.sql.Timestamp;

/**
 * Created by kelly-lee on 2017/10/27.
 */
public class DateTimeGenerater {
    public static final int[] YYMMDD = {2, 3, 5, 6, 8, 9};
    public static final int[] YYYYMMDD = {0, 1, 2, 3, 5, 6, 8, 9};
    public static final int[] YYYYMMDDHHMMSS = {0, 1, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18};
    public static final int[] MMDDHHMMSS = {5, 6, 8, 9, 11, 12, 14, 15, 17, 18};
    public static final int[] HHMMSS = {11, 12, 14, 15, 17, 18};
    public static final int[] DD = {8, 9};

    public static String get(int[] pattern, long time) {
        StringBuilder sb = new StringBuilder(pattern.length);
        String ts = new Timestamp(time).toString();
        for (int idx : pattern)
            sb.append(ts.charAt(idx));
        return sb.toString();
    }
}
