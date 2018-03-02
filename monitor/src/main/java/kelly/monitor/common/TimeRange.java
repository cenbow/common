package kelly.monitor.common;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static kelly.monitor.util.Constants.*;

// 时间段
@Setter
@Getter
@ToString
/**
 * Created by kelly-lee on 2018/2/11.
 */
public class TimeRange {

    private static final int utc_offset = (int) (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000);
    private static final int MAX_RANGE = (int) TimeUnit.DAYS.toSeconds(1) - 1;
    static final Range WHOLE_DAY = new Range(0, MAX_RANGE);

    private List<String> values;
    private List<Range> ranges = Lists.newArrayList();

    public TimeRange() {
    }

    public TimeRange(List<String> values) {
        parse(values);
    }

    public TimeRange(String value) {
        parse(Strings.isNullOrEmpty(value) ? null : SPLITTER_DOT.splitToList(value));
    }

    public String toDescription() {
        return "[" + values.stream().collect(Collectors.joining(",")) + "]";
    }

    public void parse(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            values = Lists.newArrayList();
            values.add("00:00-23:59");
        }
        values.stream().forEach(value -> {
            //00:00-14:00
            List<String> times = SPLITTER_TO.splitToList(value);
            if (times.size() < 2) {
                //非法
                return;
            }
            int from = secondOfDay(times.get(0));
            int to = secondOfDay(times.get(1));
            if (from == -1 && to == -1) {
                //非法
                return;
            }
            if (from == -1) from = 0;
            if (to == -1) to = MAX_RANGE;
            if (from > to) {//跨天
                ranges.add(new Range(from, MAX_RANGE));
                ranges.add(new Range(0, to));
            } else {
                ranges.add(new Range(from, to));
            }
        });
        if (CollectionUtils.isEmpty(ranges)) {
            ranges.add(WHOLE_DAY);
            return;
        }
    }


    private int secondOfDay(String time) {
        if (Strings.isNullOrEmpty(time)) return -1;
        List<String> items = SPLITTER_COLON.splitToList(time);
        int hour = Integer.valueOf(items.get(0));
        int minute = Integer.valueOf(items.get(1));
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return -1;
        }
        return (int) (TimeUnit.HOURS.toSeconds(hour) + TimeUnit.MINUTES.toSeconds(minute));
    }

    private int secondOfDay(long timestamp) {
        return (int) ((timestamp / 1000 + utc_offset) % TimeUnit.DAYS.toSeconds(1));
    }

    public boolean hit(Date date) {
        return hit(date.getTime());
    }

    public boolean hit(long timestamp) {
        // second of day
        int time = secondOfDay(timestamp);
        for (Range range : ranges) {
            if (range.from <= time && time <= range.to) {
                return true;
            }
        }
        return false;
    }


    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    static class Range {
        @NonNull
        private int from;
        @NonNull
        private int to;
    }

}
