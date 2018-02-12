package kelly.monitor.alert;

import com.google.common.collect.Maps;
import kelly.monitor.util.Constants;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Crted by kelly-lee on 2018/2/7.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AlertTagConfig {

    @NonNull
    String tagKey;
    List<String> tagValues = null;
    FilterType filterType = FilterType.INCLUDE;
    LogicType logicType = LogicType.ALL;
    private static final String VALUE_ALL = "*";

    enum LogicType {
        ALL, ANY
    }

    enum FilterType {
        INCLUDE("包含"), EXCLUDE("排除");
        private String text;

        FilterType(String text) {
            this.text = text;
        }
    }

    public String toDescrption() {
        return tagKey + (logicType == LogicType.ANY ? "=" : filterType.name()) + tagValues;
    }

    public AlertTagConfig(String tagKey, List<String> tagValues) {
        this(tagKey, tagValues, FilterType.INCLUDE);
    }

    public AlertTagConfig(String tagKey, List<String> tagValues, FilterType filterType) {
        this(tagKey, tagValues, filterType, LogicType.ALL);
    }

    public Map<String, String> hit(Map<String, String> tags) {
        if (MapUtils.isEmpty(tags)) return Maps.newTreeMap();
        return tags.entrySet().stream().filter(
                entry -> tagKey.equalsIgnoreCase(entry.getKey()) &&
                        (matchAll() ||
                                filterType == FilterType.INCLUDE && tagValues.contains(entry.getValue()) ||
                                filterType == FilterType.EXCLUDE && !tagValues.contains(entry.getValue())
                        )).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    public boolean match(Map<String, String> tags) {
        return matchAll() || matchExclude(tags) || matchTags(tags);
    }

    public boolean matchAll() {
        return CollectionUtils.isEmpty(tagValues) || tagValues.contains(Constants.ANY);
    }

    public boolean matchExclude(Map<String, String> tags) {
        return filterType == FilterType.EXCLUDE && (MapUtils.isEmpty(tags) || !tags.keySet().contains(tagKey));
    }

    public boolean matchTags(Map<String, String> tags) {
        return MapUtils.isNotEmpty(tags) && tags.entrySet().stream()
                .filter(
                        entry -> {
                            return tagKey.equalsIgnoreCase(entry.getKey()) && (
                                    filterType == FilterType.INCLUDE && tagValues.contains(entry.getValue()) ||
                                            filterType == FilterType.EXCLUDE && !tagValues.contains(entry.getValue())
                            );
                        }).findAny().isPresent();
    }


}
