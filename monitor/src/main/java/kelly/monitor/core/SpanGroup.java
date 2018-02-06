package kelly.monitor.core;

import com.google.common.collect.Lists;
import kelly.monitor.common.AggregatorType;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;

import java.io.Serializable;
import java.util.*;

/**
 * Groups multiple spans together and offers a dynamic "view" on them.
 * <p>
 * This is used for queries to the TSDB, where we might group multiple
 * {@link Span}s that are for the same time series but different tags together.
 * We need to "hide" data points that are outside of the time period of the
 * query and do on-the-fly aggregation of the data points coming from the
 * different Spans, using an {@link Aggregator}. Since not all the Spans will
 * have their data points at exactly the same time, we also do on-the-fly linear
 * interpolation. If needed, this view can also return the rate of change
 * instead of the actual data points.
 * <p>
 * The implementation can also dynamically downsample the data when a sampling
 * interval a downsampling function (in the form of an {@link Aggregator}) are
 * given.
 */
public class SpanGroup implements Constant, DataPoints, Serializable {

    /** Start time (UNIX timestamp in seconds) on 32 bits ("unsigned" int). */
    private final long start_time;

    /** End time (UNIX timestamp in seconds) on 32 bits ("unsigned" int). */
    private final long end_time;

    /**
     * The tags of this group. This is the intersection set between the tags of
     * all the Spans in this group.
     *
     * @see #computeTags
     */
    private Map<String, String> tags;

    /**
     * The names of the tags that aren't shared by every single data point. This
     * is the symmetric difference between the tags of all the Spans in this
     * group.
     *
     * @see #computeTags
     */
    private List<String> aggregated_tags;

    /** Spans in this group. They must all be for the same metric. */
    private final List<Span> spans = Lists.newArrayList();

    /** Aggregator to use to aggregate data points from different Spans. */
    private Aggregator aggregator;

    /**
     * Downsampling function to use, if any (can be {@code null}). If this is
     * non-null, {@code sample_interval} must be strictly positive.
     */
    private Aggregator downsampler;

    /** Minimum time interval (in seconds) wanted between each data point. */
    private final int sample_interval;

    /**
     *
     * @param start_time Any data point strictly before this timestamp will be
     * ignored.
     * @param end_time Any data point strictly after this timestamp will be
     * ignored.
     * @param spans A sequence of initial to add to this group.
     * Ignored if {@code null}. Additional spans can be added with {@link #add}.
     *
     * @param aggregator The aggregation function to use.
     * @param downsampler Aggregation function to use to group data points
     * within an interval.
     */
    public SpanGroup(long start_time, long end_time, Iterable<Span> spans, final AggregatorType aggregator,
            final AggregatorType downsampler, final int samplerInterval) {
        this.start_time = start_time;
        this.end_time = end_time;

        if (spans != null) {
            for (Span span : spans) {
                add(span);
            }
        }
        this.aggregator = aggregator == null ? null : Aggregators.get(aggregator);
        this.downsampler = downsampler == null ? null : Aggregators.get(downsampler);
        this.sample_interval = samplerInterval;
    }

    public void add(Span span) {
        if (tags != null) {
            throw new AssertionError("The set of tags has already been computed, you can't add more Spans");
        }

        if (span.timestamp(0) <= end_time && span.timestamp(span.size() - 1) >= start_time) {
            this.spans.add(span);
        }
    }

    public List<Span> getSpans() {
        return spans;
    }

    public void setDownsampler(Aggregator downsampler) {
        this.downsampler = downsampler;
    }

    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    private void computeTags() {
        if (spans.isEmpty()) {
            tags = new HashMap<String, String>(0);
            aggregated_tags = new ArrayList<String>(0);
            return;
        }

        Iterator<Span> it = spans.iterator();

        // first span
        tags = new HashMap<String, String>(it.next().getTags());
        final HashSet<String> discarded_tags = new HashSet<String>();

        while (it.hasNext()) {
            Map<String, String> nextTags = it.next().getTags();

            Iterator<Map.Entry<String, String>> i = tags.entrySet().iterator();

            while (i.hasNext()) {
                final Map.Entry<String, String> entry = i.next();
                final String name = entry.getKey();
                // current tags exists in nexttags?
                final String value = nextTags.get(name);
                if (value == null || !value.equals(entry.getValue())) {
                    i.remove();
                    discarded_tags.add(name);
                }
            }
        }

        aggregated_tags = new ArrayList<String>(discarded_tags);
    }

    @Override
    public String metricName() {
        return spans.isEmpty() ? "" : spans.get(0).metricName();
    }

    @Override
    public MetricType metricType() {
        return spans.isEmpty() ? null : spans.get(0).metricType();
    }

    @Override
    public Map<String, String> getTags() {
        if (tags == null) {
            computeTags();
        }
        return tags;
    }

    public List<String> getAggregatedTags() {
        if (tags == null) {
            computeTags();
        }
        return aggregated_tags;
    }

    @Override
    public int size() {
        final SGIterator it = new SGIterator(null);
        int size = 0;
        while (it.hasNext()) {
            it.next();
            size++;
        }
        return size;
    }

    public int aggregatedSize() {
        int size = 0;
        for (final Span span : spans) {
            size += span.size();
        }
        return size;
    }

    private DataPoint getDataPoint(int i, ValueType valueType) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("negative index: " + i);
        }
        final int saved_i = i;
        final SGIterator it = new SGIterator(valueType);
        DataPoint dp = null;
        while (it.hasNext() && i >= 0) {
            dp = it.next();
            i--;
        }
        if (i != -1 || dp == null) {
            throw new IndexOutOfBoundsException("index " + saved_i + " too large (it's >= " + size() + ") for " + this);
        }
        return dp;
    }

    @Override
    public long timestamp(int i) {
        return getDataPoint(i, null).timestamp();
    }

    @Override
    public float value(int i, ValueType valueType) {
        return getDataPoint(i, valueType).value();
    }

    public SGIterator iterator(ValueType valueType) {
        return new SGIterator(valueType);
    }

    private final class SGIterator extends AbstractSeekableView implements DataPoint, Aggregator.Floats {

        private static final long TIME_MASK = 0x7FFFFFFFFFFFFFFFL;

        private final SeekableView[] iterators;

        private final long[] timestamps;

        private final float[] values;

        /** The index in {@link #iterators} of the current Span being used. */
        private int current;

        /** The index in {@link #values} of the current value being aggregated. */
        private int pos;

        SGIterator(ValueType valueType) {
            super(valueType);

            int size = spans.size();
            this.iterators = new SeekableView[size];
            timestamps = new long[size * 2];
            values = new float[size * 2];

            for (int i = 0; i < size; i++) {
                SeekableView it;
                if (downsampler == null || sample_interval == 0 || sample_interval <= 60) {
                    it = spans.get(i).iterator(valueType);
                } else {
                    it = spans.get(i).downsampler(sample_interval, downsampler, valueType);
                }

                iterators[i] = it;
                it.seek(start_time);
                final DataPoint dp;
                try {
                    dp = it.next();
                } catch (NoSuchElementException e) {
                    throw new AssertionError("Span #" + i + " is empty! span=" + spans.get(i));
                }
                if (dp.timestamp() >= start_time) {
                    putDataPoint(size + i, dp);
                } else {
                    endReached(i);
                }
            }
        }

        private void endReached(final int i) {
            timestamps[iterators.length + i] = TIME_MASK;
            iterators[i] = null;
        }

        private void putDataPoint(int i, DataPoint dp) {
            timestamps[i] = dp.timestamp();
            values[i] = dp.value();
        }

        @Override
        public boolean hasNext() {
            final int size = iterators.length;
            for (int i = 0; i < size; i++) {
                if ((timestamps[size + i] & TIME_MASK) <= end_time) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public DataPoint next() {
            int size = iterators.length;
            long min_ts = Long.MAX_VALUE;
            // 去掉已经end的
            for (int i = current; i < size; i++) {
                if (timestamps[i + size] == TIME_MASK) {
                    timestamps[i] = 0;
                }
            }

            // Now we need to find which Span we'll consume next. We'll pick the
            // one that has the data point with the smallest timestamp since we
            // want to
            // return them in chronological order.
            current = -1;
            // If there's more than one Span with the same smallest timestamp,
            // we'll
            // set this to true so we can fetch the next data point in all of
            // them at
            // the same time.
            boolean multiple = false;
            for (int i = 0; i < size; i++) {
                long timestamp = timestamps[size + i] & TIME_MASK;
                if (timestamp <= end_time) {
                    if (timestamp < min_ts) {
                        min_ts = timestamp;
                        current = i;
                        // We just found a new minimum so right now we can't
                        // possibly have
                        // multiple Spans with the same minimum.
                        multiple = false;
                    } else if (timestamp == min_ts) {
                        multiple = true;
                    }
                }
            }
            if (current < 0) {
                throw new NoSuchElementException("no more elements");
            }
            moveToNext(current);
            if (multiple) {
                // We know we saw at least one other data point with the same
                // minimum
                // timestamp after `current', so let's move those ones too.
                for (int i = current + 1; i < size; i++) {
                    long timestamp = timestamps[size + i] & TIME_MASK;
                    if (timestamp == min_ts) {
                        moveToNext(i);
                    }
                }
            }

            return this;
        }

        /**
         * Makes iterator number {@code i} move forward to the next data point.
         *
         * @param i The index in {@link #iterators} of the iterator.
         */
        private void moveToNext(int i) {

            int next = iterators.length + i;

            timestamps[i] = timestamps[next];
            values[i] = values[next];

            SeekableView it = iterators[i];
            if (it.hasNext()) {
                putDataPoint(next, it.next());
            } else {
                endReached(i);
            }
        }

        @Override
        public void seek(long timestamp) {
            for (SeekableView it : iterators) {
                it.seek(timestamp);
            }
        }

        @Override
        public long timestamp() {
            return timestamps[current] & TIME_MASK;
        }

        @Override
        public float value() {
            pos = -1;
            final float value = aggregator.run(this);
            if (value != value || Float.isInfinite(value)) {
                throw new IllegalStateException("Got NaN or Infinity: " + value + " in this " + this);
            }
            return value;
        }

        @Override
        public boolean hasNextValue() {
            return hasNextValue(false);
        }

        @Override
        public float nextValue() {
            if (hasNextValue(true)) {
                final float y0 = values[pos];
                if (current == pos) {
                    return y0;
                }
                long x = timestamps[current] & TIME_MASK;
                long x0 = timestamps[pos] & TIME_MASK;
                if (x == x0) {
                    return y0;
                }
                int next = pos + iterators.length;
                float y1 = values[next];
                long x1 = timestamps[next] & TIME_MASK;
                if (x == x1) {
                    return y1;
                }
                float r = y0 + (x - x0) * (y1 - y0) / (x1 - x0);
                if ((x1 & 0xFFFFFFFF00000000L) != 0) {
                    throw new AssertionError("x1=" + x1 + " in " + this);
                }
                return r;
            }
            throw new NoSuchElementException("no more values in " + this);
        }

        /**
         * Returns whether or not there are more values to aggregate.
         *
         * @param update_pos Whether or not to also move the internal pointer
         * {@link #pos} to the index of the next value to aggregate.
         * @return true if there are more values to aggregate, false otherwise.
         */
        private boolean hasNextValue(boolean update_pos) {
            final int size = iterators.length;
            for (int i = pos + 1; i < size; i++) {
                if (timestamps[i] != 0) {
                    if (update_pos) {
                        pos = i;
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
