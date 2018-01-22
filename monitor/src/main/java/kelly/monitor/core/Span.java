package kelly.monitor.core;

import com.google.common.collect.Lists;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.uid.UniqueId;
import org.hbase.async.KeyValue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a read-only sequence of continuous data points.
 * <p>
 * This class stores a continuous sequence of {@link RowSeq}s in memory.
 */
public class Span implements DataPoints, Serializable {

    private UniqueId uniqueId;
    private List<RowSeq> rows = Lists.newArrayList();

    public Span(UniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<RowSeq> getRows() {
        return rows;
    }

    @Override
    public String metricName() {
        checkNotEmpty();
        return rows.get(0).metricName();
    }

    @Override
    public MetricType metricType() {
        checkNotEmpty();
        return rows.get(0).metricType();
    }

    @Override
    public Map<String, String> getTags() {
        checkNotEmpty();
        return rows.get(0).getTags();
    }

    @Override
    public List<String> getAggregatedTags() {
        return Collections.emptyList();
    }

    private void checkNotEmpty() {
        if (rows.size() == 0) {
            throw new IllegalStateException("empty Span");
        }
    }

    public int aggregatedSize() {
        return 0;
    }

    public int size() {
        int size = 0;
        for (RowSeq row : rows) {
            size += row.size();
        }
        return size; // time point count
    }

    public void addRow(RowSeq row) {
        rows.add(row);
    }

    public void addRow(KeyValue keyValue, int qualifierWidth) {
        RowSeq row = new RowSeq(uniqueId, keyValue, qualifierWidth);
        addRow(row);
    }


    public long timestamp(final int i) {
        final long idxoffset = getIdxOffsetFor(i);
        final int idx = (int) (idxoffset >>> 32);
        final int offset = (int) (idxoffset & 0x00000000FFFFFFFF);
        return rows.get(idx).timestamp(offset);
    }

    public float value(final int i, final ValueType valueType) {
        final long idxoffset = getIdxOffsetFor(i);
        final int idx = (int) (idxoffset >>> 32);
        final int offset = (int) (idxoffset & 0x00000000FFFFFFFF);
        return rows.get(idx).value(offset, valueType);
    }

    protected long getIdxOffsetFor(final int i) {

        int idx = 0;
        int offset = 0;

        for (final DataPoints points : rows) {
            final int size = points.size();
            if (offset + size > i) {
                break;
            }
            offset += size;
            idx++;
        }
        return ((long) idx << 32) | (i - offset);
    }

    /**
     * Finds the index of the row in which the given timestamp should be.
     *
     * @param timestamp A strictly positive 32-bit integer.
     * @return A strictly positive index in the {@code rows} array.
     */
    private int seekRow(long timestamp) {
        int rowIndex = 0;
        final int nrows = rows.size();
        for (RowSeq row : rows) {
            int sz = row.size();
            if (row.timestamp(sz - 1) < timestamp) {
                rowIndex++;
            } else {
                break;
            }
        }
        if (rowIndex == nrows) {
            --rowIndex;
        }
        return rowIndex;
    }

    /** Package private iterator method to access it as a Span.Iterator. */
    public Span.Iterator iterator(ValueType valueType) {
        return new Span.Iterator(valueType);
    }

    class Iterator extends AbstractSeekableView {

        /** Index of the {@link RowSeq} we're currently at, in {@code rows}. */
        protected int row_index;

        protected RowSeq.Iterator current_row;

        Iterator(ValueType valueType) {
            super(valueType);
            current_row = rows.get(0).iterator(valueType);
        }

        @Override
        public boolean hasNext() {
            // more points in this row or more rows
            return (current_row.hasNext() || row_index < rows.size() - 1);
        }

        @Override
        public DataPoint next() {
            if (current_row.hasNext()) {
                return current_row.next();
            } else if (row_index < rows.size() - 1) {
                row_index++;
                current_row = rows.get(row_index).iterator(valueType);
                return current_row.next();
            }

            throw new NoSuchElementException("no more elements");
        }

        @Override
        public void seek(long timestamp) {
            int row_index = seekRow(timestamp);
            if (row_index != this.row_index) {
                this.row_index = row_index;
                current_row = rows.get(row_index).iterator(valueType);
            }
            current_row.seek(timestamp);
        }
    }

    /** Package private iterator method to access it as a DownsamplingIterator. */
    Span.DownsamplingIterator downsampler(final int interval, final Aggregator downsampler, final ValueType valueType) {
        return new Span.DownsamplingIterator(interval, downsampler, valueType);
    }

    /**
     * Iterator that downsamples the data using an {@link Aggregator}.
     * <p>
     * This implementation relies on the fact that the {@link RowSeq}s in this
     * {@link Span} have {@code O(1)} access to individual data points, in order
     * to be efficient.
     */
    final class DownsamplingIterator extends Span.Iterator implements DataPoint, Aggregator.Floats {

        /** The "sampling" interval, in seconds. */
        private final int interval;

        /** Function to use to for downsampling. */
        private final Aggregator downsampler;

        /** Current timestamp */
        private long timestamp;

        /** Current value. */
        private float value;

        /**
         * DownsamplingIterator.
         *
         * @param interval The interval in seconds wanted between each data
         * point.
         * @param downsampler The downsampling function to use.
         */
        DownsamplingIterator(final int interval, final Aggregator downsampler, final ValueType valueType) {
            super(valueType);
            this.interval = interval;
            this.downsampler = downsampler;
        }

        @Override
        public boolean hasNext() {
            return (current_row.hasNext() || row_index < rows.size() - 1);
        }

        private boolean moveToNext() {
            if (!current_row.hasNext()) {
                // Yes, move on to the next one.
                if (row_index < rows.size() - 1) {
                    current_row = rows.get(++row_index).iterator(valueType);
                    current_row.next();
                    return true;
                } else {// No more rows, can't go further.
                    return false;
                }
            }

            current_row.next();
            return true;
        }

        @Override
        public DataPoint next() {

            if (!hasNext()) {
                throw new NoSuchElementException("no more data points in " + this);
            }

            // Look ahead to see if all the data points that fall within the next
            // interval turn out to be integers.  While we do this, compute the
            // average timestamp of all the datapoints in that interval.
            long newtime = 0;
            final int saved_row_index = row_index;
            final long saved_state = current_row.saveState();
            // Since we know hasNext() returned true, we have at least 1 point.
            moveToNext();

            timestamp = current_row.timestamp() + interval; // end of this interval.

            int npoints = 0;
            do {
                npoints++;
                newtime += current_row.timestamp();
            } while (moveToNext() && current_row.timestamp() < timestamp);
            // average time
            newtime /= npoints;

            // Now that we're done looking ahead, let's go back where we were.
            if (row_index != saved_row_index) {
                row_index = saved_row_index;
                current_row = rows.get(row_index).iterator(valueType);
            }
            current_row.restoreState(saved_state);

            // compute 'value'
            value = downsampler.run(this);

            timestamp = newtime;
            return this;
        }

        // DataPoint
        @Override
        public long timestamp() {
            return timestamp;
        }

        public float value() {
            return value;
        }

        @Override
        public boolean hasNextValue() {
            if (!current_row.hasNext()) {
                return row_index < rows.size() - 1 && rows.get(row_index + 1).timestamp(0) < timestamp;
            }
            return current_row.peekNextTimestamp() < timestamp;
        }

        @Override
        public float nextValue() {
            if (hasNext()) {
                moveToNext();
                return current_row.value();
            }
            throw new NoSuchElementException("no more floats in interval of " + this);
        }
    }
}
