package kelly.monitor.core;


import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;

import java.util.List;
import java.util.Map;


/**
 * Represents a read-only sequence of continuous data points.
 * <p>
 * Implementations of this interface aren't expected to be synchronized.
 */
public interface DataPoints {

    String metricName();

    MetricType metricType();

    /**
     * Returns the tags associated with these data points.
     * 
     * @return A non-{@code null} map of tag names (keys), tag values (values).
     */
    Map<String, String> getTags();

    /**
     * Returns the tags associated with some but not all of the data points.
     * <p>
     * When this instance represents the aggregation of multiple time series
     * (same metric but different tags), {@link #getTags} returns the tags that
     * are common to all data points (intersection set) whereas this method
     * returns all the tags names that are not common to all data points (union
     * set minus the intersection set, also called the symmetric difference).
     * <p>
     * If this instance does not represent an aggregation of multiple time
     * series, the list returned is empty.
     * 
     * @return A non-{@code null} list of tag names.
     */
    List<String> getAggregatedTags();

    /**
     * Returns the number of data points.
     * 
     * @return A positive integer.
     */
    int size();

    /**
     * Returns the number of data points aggregated in this instance.
     * <p>
     * When this instance represents the aggregation of multiple time series
     * (same metric but different tags), {@link #size} returns the number of
     * data points after aggregation, whereas this method returns the number of
     * data points before aggregation.
     * <p>
     * If this instance does not represent an aggregation of multiple time
     * series, then 0 is returned.
     * 
     * @return A positive integer.
     */
    int aggregatedSize();

    /**
     * Returns the timestamp associated with the {@code i}th data point. The
     * first data point has index 0.
     * 
     * <p>
     * It is guaranteed that
     * 
     * <pre>
     * timestamp(i) &lt; timestamp(i + 1)
     * </pre>
     * 
     * @return A strictly positive integer.
     */
    long timestamp(int i);

    /**
     * Returns the specified type value of the {@code i}th data points
     * 
     * @param i
     * @param valueType
     * @return
     */
    float value(int i, ValueType valueType);

    /**
     * Iterate all value of the specified type.
     * 
     * @param valueType
     * @return
     */
    SeekableView iterator(ValueType valueType);
}
