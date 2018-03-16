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

    private static final int UTC_OFFSET = (int) (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000);
    private static final int DEFAULT_FROM = 0;
    private static final int DEFAULT_TO = (int) TimeUnit.DAYS.toSeconds(1) - 1;
    private static final String DEFAULT_FROM_TEXT = "0:00";
    private static final String DEFAULT_TO_TEXT = "23:59";
    static final Range WHOLE_DAY = new Range(DEFAULT_FROM_TEXT, DEFAULT_TO_TEXT, DEFAULT_FROM, DEFAULT_TO);

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
        return "[" + ranges.stream().map(range -> range.fromText + "-" + range.toText).collect(Collectors.joining(",")) + "]";
    }


    public void parse(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            values = Lists.newArrayList();
            values.add(DEFAULT_FROM_TEXT + "-" + DEFAULT_TO_TEXT);
        }
        values.stream().forEach(value -> {
            //0:00-14:00
            List<String> times = SPLITTER_TO.splitToList(value);
            String fromText = times.get(0);
            String toText = times.get(1);
            if (times.size() < 2) {
                //非法
                return;
            }
            int from = secondOfDay(fromText);
            int to = secondOfDay(toText);
            if (from == -1 && to == -1) {
                //非法
                return;
            }
            if (from == -1) from = DEFAULT_FROM;
            if (to == -1) to = DEFAULT_TO;
            if (from > to) {//跨天
                ranges.add(new Range(fromText, DEFAULT_TO_TEXT, from, DEFAULT_TO));
                ranges.add(new Range(DEFAULT_FROM_TEXT, toText, DEFAULT_FROM, to));
            } else {
                ranges.add(new Range(fromText, toText, from, to));
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
        return (int) ((timestamp / 1000 + UTC_OFFSET) % TimeUnit.DAYS.toSeconds(1));
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
        private String fromText;
        @NonNull
        private String toText;
        @NonNull
        private int from;
        @NonNull
        private int to;
    }

}
