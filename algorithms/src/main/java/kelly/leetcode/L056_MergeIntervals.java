package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/24.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Definition for an interval.
 * public class Interval {
 * int start;
 * int end;
 * Interval() { start = 0; end = 0; }
 * Interval(int s, int e) { start = s; end = e; }
 * }
 */
public class L056_MergeIntervals {

    public List<Interval> merge(List<Interval> intervals) {
        Collections.sort(intervals, new Comparator<Interval>() {
            public int compare(Interval o1, Interval o2) {
                return o1.start - o2.start;
            }
        });

        List<Interval> result = new ArrayList<Interval>();
        Interval prev = null;
        for (Interval cur : intervals) {
            if (prev == null || prev.end < cur.start) {
                prev = new Interval(cur.start, cur.end);
                result.add(prev);
            } else {
                prev.end = Math.max(prev.end, cur.end);
            }
        }
        return result;
    }
}