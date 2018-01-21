package kelly.monitor.metric.key;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by kelly.li on 18/1/20.
 */
public class MetricTags {

    /**
     * 当添加重复的tag key,tag value 被覆盖
     */
    private ConcurrentMap<String, MetricTag> tags = Maps.newConcurrentMap();
    /**
     * 全局缓存相同的tag kv
     */
    private static Cache<String, MetricTag> tagCache = CacheBuilder.newBuilder().build();

    private static final Joiner JOINER = Joiner.on(",").skipNulls();


    public MetricTag add(String key, String value) {
        MetricTag metricTag = null;
        try {
            metricTag = tagCache.get(cacheKey(key, value), () -> MetricTag.of(key, value));
        } catch (ExecutionException e) {
            metricTag = tagCache.getIfPresent(key);
        }
        MetricTag old = tags.put(key, metricTag);
        return old == null ? old : metricTag;
    }

    public String format() {
        return JOINER.join(tags.values().stream().map(tag -> tag.format()).collect(Collectors.toSet()));
    }


    static String cacheKey(String key, String value) {
        return key + "=" + value;
    }

    @Override
    public String toString() {
        return "MetricTags{" +
                "tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricTags metricTags = (MetricTags) o;
        return Objects.equal(tags, metricTags.tags);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tags);
    }
}
