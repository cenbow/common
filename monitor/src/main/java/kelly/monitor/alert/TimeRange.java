package kelly.monitor.alert;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

// 时间段
public class TimeRange {

    private static final int utc_offset = (int) (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000);
    private static final int MAX_RANGE = 86399;
    private static final List<int[]> ALLDAY = new ArrayList<int[]>(1);

    static {
        ALLDAY.add(new int[]{0, MAX_RANGE});
    }

    private String range;
    // 秒数 段
    private final List<int[]> slices;

    public TimeRange() {
        this(null);
    }

    public TimeRange(String range) {
        this.range = range;
        this.slices = parse(range);
    }

    public static void main(String[] args) {
        TimeRange timeRange = new TimeRange("00:00-12:59,14:00-16:00");
        System.out.println(timeRange.buildBeautifulString());
    }

    /*  public static boolean checkTimeRange(String range){
          TimeRange timeRange;
          try{
              timeRange = new TimeRange(range);
          }catch (Exception e){
              return false;
          }
          if(timeRange.slices==null||timeRange.slices.isEmpty()){
              return false;
          }
          return true;
      }*/
    private static List<int[]> parse(String range) {

        if (range == null || range.isEmpty()) {
            return ALLDAY;
        }

        List<int[]> slices = new ArrayList<int[]>();

        for (String time : range.split(",")) {
            int idx = time.indexOf('-');
            if (idx < 0) {
                throw new IllegalStateException("非法的时间表达式");
            }

            int from = secondOfDay(time.substring(0, idx).trim());
            int to = secondOfDay(time.substring(idx + 1).trim());

            if (from == -1 && to == -1) {
                throw new IllegalStateException("非法的时间表达式");
            }

            if (to == -1) {
                to = MAX_RANGE;
            } else if (from == -1) {
                from = 0;
            }

            if (from > to) {
                // 跨天分两段
                slices.add(new int[]{from, MAX_RANGE});
                slices.add(new int[]{0, to});
            } else {
                slices.add(new int[]{from, to});
            }
        }
        if (slices.size() == 0) {
            throw new IllegalStateException("非法的时间表达式");
        }
        return slices;
    }

    private static int secondOfDay(String time) {

        if (time == null || time.isEmpty()) {
            return -1;
        }

        int idx = time.indexOf(':');

        int hour = Integer.valueOf(time.substring(0, idx));
        int minute = Integer.valueOf(time.substring(idx + 1));

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return -1;
        }
        // hh:mm
        return hour * 3600 + minute * 60;
    }

    public boolean hit(Date date) {
        return hit(date.getTime());
    }

    public boolean hit(long timestamp) {

        // second of day
        int time = (int) ((timestamp / 1000 + utc_offset) % 86400);

        for (int[] range : slices) {
            if (range[0] <= time && time <= range[1]) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(TimeRange timeRange) {
        for (int[] slice : timeRange.slices) {
            for (int[] slice2 : this.slices) {
                //最小值不能在区间内
                if ((slice[0] >= slice2[0]) && (slice[0] < slice2[1])) {
                    return true;
                }
                //最大值不能在区间内
                if ((slice[1] > slice2[0]) && (slice[1] <= slice2[1])) {
                    return true;

                }
                //小值比区间小，则大值不能比区间最小值小
                if (slice[0] <= slice2[0] && slice[1] >= slice2[1]) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRange() {
        return range;
    }

    @Override
    public String toString() {
        return getRange();
    }

    public String buildBeautifulString() {
        StringBuilder sliceString = new StringBuilder("[");
        for (int[] slice : slices) {
            if (slice != null && slice.length == 2) {
                sliceString.append("start:")
                        .append(slice[0])
                        .append(",end:")
                        .append(slice[1]);
            } else if (slice == null) {
                sliceString.append("null");
            } else {
                sliceString.append(Joiner.on(",").join(ArrayUtils.toObject(slice)));
            }
            sliceString.append("; ");
        }
        sliceString.append("]");

        return Objects.toStringHelper(TimeRange.class)
                .add("range", getRange())
                .add("slices", sliceString.toString()).toString();
    }

}
