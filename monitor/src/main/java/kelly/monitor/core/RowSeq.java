package kelly.monitor.core;


import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.uid.UniqueId;
import org.hbase.async.Bytes;
import org.hbase.async.KeyValue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a read-only sequence of continuous HBase rows.
 * <p>
 * This class stores in memory the data of one or more continuous HBase rows for
 * a given time series.
 */
public class RowSeq implements DataPoints, Constant, Serializable {

    private final UniqueId uniqueId;

    /** First row key. */
    private final byte[] key;

    /** Qualifiers for individual data points. */
    private final byte[] qualifiers;

    /** Values in the row. */
    private final byte[] values;

    // TODO move duplicate computation here.
    private final MetricType metricType;
    private final String metricName;
    private final long base_time;
    /** How many values each time point has */
    private final int value_count;
    /** How many bytes each time point has */
    private final int value_width;

    private final int qualifierWidth;

    public RowSeq(UniqueId uniqueId, KeyValue kv, int qualifierWidth) {
        this.uniqueId = uniqueId;
        this.key = kv.key();
        this.qualifiers = kv.qualifier();
        this.values = kv.value();

        metricType = metricType();
        metricName = metricName();
        base_time = RowKey.baseTime(key);
        value_count = metricType.sequence().length;
        value_width = value_count * ONE_VALUE_WIDTH;
        this.qualifierWidth = qualifierWidth;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getQualifiers() {
        return qualifiers;
    }

    public int getQualifierWidth() {
        return qualifierWidth;
    }

    public byte[] getValues() {
        return values;
    }

    public int getValue_width() {
        return value_width;
    }

    public String metricName() {
        return RowKey.metricName(uniqueId, key);
    }

    public MetricType metricType() {
        return RowKey.metricType(key);
    }

    public Map<String, String> getTags() {
        return Tags.getTags(uniqueId, this.key);
    }

    @Override
    public List<String> getAggregatedTags() {
        return Collections.emptyList();
    }

    public float value(int i, ValueType valueType) {
        if (!metricType.contains(valueType)) {
            throw new IllegalArgumentException("metric(" + metricName + " : " + metricType
                    + ") does not contains value type:" + valueType);
        }
        Iterator it = iterator(valueType);
        while (i-- >= 0) {
            it.next();
        }
        return it.value();
    }

    @Override
    public long timestamp(int i) {
        return base_time + getQualValue(qualifiers, i * qualifierWidth);
    }

    public int aggregatedSize() {
        return 0;
    }

    public int size() {
        return qualifiers.length / qualifierWidth;
    }

    @Override
    public Iterator iterator(ValueType valueType) {
        return new Iterator(valueType);
    }

    /** Iterator for {@link RowSeq}s. */
    final class Iterator extends AbstractSeekableView implements DataPoint {

        /** Current qualifier. */
        private int qualifier;

        /** Next index in {@link #qualifiers}. */
        private int qual_index;

        /** Next index in {@link #values}. */
        private int value_index; // short is not enough

        private int offset;

        public Iterator(ValueType valueType) {
            super(valueType);
            offset = metricType.indexOf(valueType) * ONE_VALUE_WIDTH;
            value_index = offset;
        }

        @Override
        public boolean hasNext() {
            return qual_index < qualifiers.length;
        }

        @Override
        public DataPoint next() {
            if (!hasNext()) {
                throw new NoSuchElementException("no more elements");
            }

            logger.debug("qualifiers length={}, qual_index={}, qualifiers={}", qualifiers.length, qual_index, qualifiers);

            qualifier = getQualValue(qualifiers, qual_index);

            qual_index += qualifierWidth;
            value_index += value_width;

            return this;
        }

        @Override
        public void seek(long timestamp) {
            qual_index = 0;
            value_index = offset;
            final int len = qualifiers.length;
            while (qual_index < len && peekNextTimestamp() < timestamp) {
                qual_index += qualifierWidth;
                value_index += value_width;
            }
            if (qual_index > 0) {
                qualifier = getQualValue(qualifiers, qual_index - qualifierWidth);
            }
        }

        @Override
        public long timestamp() {
            return base_time + (qualifier & 0xFFFFFFF);
        }

        @Override
        public float value() {
            return Float.intBitsToFloat(Bytes.getInt(values, value_index - value_width));
        }

        // ---------------- //
        // Helpers for Span //
        // ---------------- //

        /** Helper to take a snapshot of the state of this iterator. */
        // qual_index | value_index
        long saveState() {
            return ((long) qual_index << 32) | (value_index & 0xFFFFFFFF);
        }

        /** Helper to restore a snapshot of the state of this iterator. */
        void restoreState(long state) {
            value_index = (int) (state & 0xFFFFFFFF);
            state >>>= 32;
            qual_index = (int) state;
            qualifier = 0;
        }

        /**
         * Look a head to see the next timestamp.
         *
         * @throws IndexOutOfBoundsException if we reached the end already.
         */
        long peekNextTimestamp() {
            return base_time + getQualValue(qualifiers, qual_index);
        }
    }

    private int getQualValue(byte[] qualifiers, int qual_index) {
        if(qualifierWidth > 2) {
            return Bytes.getInt(qualifiers, qual_index);
        }else {
            return Bytes.getUnsignedShort(qualifiers, qual_index) & 0xffff;
        }
    }
}
