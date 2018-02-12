package kelly.monitor.util;

import com.google.common.collect.ImmutableMap;

import java.sql.Timestamp;
import java.util.Map;

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
    public static final int[] YY_MM_DD_HH_MM_SS = {2, 3, -1, 5, 6, -1, 8, 9, -3, 11, 12, -2, 14, 15, -2, 17, 18};
    public static final int[] HH_MM_SS = {11, 12, -2, 14, 15, -2, 17, 18};
    public static final Map<Integer, String> DIC = ImmutableMap.of(-1, "-", -2, ":", -3, " ");

    public static String get(int[] pattern, long time) {
        StringBuilder sb = new StringBuilder(pattern.length);
        String ts = new Timestamp(time).toString();
        for (int idx : pattern)
            sb.append(idx < 0 ? DIC.get(idx) : ts.charAt(idx));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(get(YY_MM_DD_HH_MM_SS, System.currentTimeMillis()));
    }
}
