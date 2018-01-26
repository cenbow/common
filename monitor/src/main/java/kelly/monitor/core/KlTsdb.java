package kelly.monitor.core;


import com.stumbleupon.async.Deferred;
import kelly.monitor.common.MetricType;
import org.hbase.async.HBaseException;

import java.util.Map;

/**
 * Central class of KlTsdb.
 * 
 * You use it to add new data points or query the database.
 * <ul>
 * <li>key = metrics id(3)|unixtime hour(4)|tags id(6) * n|type(1)
 * <li>qualifier = unixtime % 3600
 * <li>value = point, depends on metrics type => value type pattern.
 * </ul>
 */
public interface KlTsdb {

    /**
     * 新增一组监控数据
     * 
     * @param metric 指标名
     * @param type 指标类型，参考{@link MetricType}
     * @param timestamp 原始时间戳
     * @param values 监控数据
     * @param tags 标签
     * @return A deferred object that indicates the completion of the request.
     * The {@link Object} has not special meaning and can be {@code null} (think
     * of it as {@code Deferred<Void>}). But you probably want to attach at
     * least an errback to this {@code Deferred} to handle failures.
     */
    Deferred<Object> addPoints(String metric, MetricType type, long timestamp, Float[] values, Map<String, String> tags);

    /**
     * Forces a flush of any un-committed in memory data.
     * <p>
     * For instance, any data point not persisted will be sent to HBase.
     * 
     * @return A {@link Deferred} that will be called once all the un-committed
     * data has been successfully and durably stored. The value of the deferred
     * object return is meaningless and unspecified, and can be {@code null}.
     * @throws HBaseException (deferred) if there was a problem sending
     * un-committed data to HBase. Please refer to the {@link HBaseException}
     * hierarchy to handle the possible failures. Some of them are easily
     * recoverable by retrying, some are not.
     */
    Deferred<Object> flush();

    /**
     * Gracefully shuts down this instance.
     * <p>
     * This does the same thing as {@link #flush} and also releases all other
     * resources.
     * 
     * @return A {@link Deferred} that will be called once all the un-committed
     * data has been successfully and durably stored, and all resources used by
     * this instance have been released. The value of the deferred object return
     * is meaningless and unspecified, and can be {@code null}.
     * @throws HBaseException (deferred) if there was a problem sending
     * un-committed data to HBase. Please refer to the {@link HBaseException}
     * hierarchy to handle the possible failures. Some of them are easily
     * recoverable by retrying, some are not.
     */
    Deferred<Object> shutdown();
}
