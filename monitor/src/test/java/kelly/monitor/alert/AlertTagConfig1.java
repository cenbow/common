package kelly.monitor.alert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import kelly.monitor.util.Constants;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Crted by kelly-lee on 2018/2/7.
 */

@Setter
@Getter
@ToString
public class AlertTagConfig1 {

    @NonNull
    String tagKey;
    List<String> tagValues = null;
    FilterType filterType = FilterType.INCLUDE;
    LogicType logicType = LogicType.ALL;
    private static final String VALUE_ALL = "*";

//    @JsonCreator
//    public AlertTagConfig1() {
//    }
//
//    @JsonCreator
//    public AlertTagConfig1(@JsonProperty("filterType") FilterType filterType, @JsonProperty("tagKey") String tagKey, @JsonProperty("tagValues") List<String> tagValues) {
//    }

//    @JsonCreators
//    public AlertTagConfig1(@JsonProperty("tagKey") String tagKey, @JsonProperty("tagValues") List<String> tagValues) {
//    }

    @JsonCreator
    public AlertTagConfig1(@JsonProperty("tagKey") String tagKey) {
//        this(null, tagKey);
    }
//
//    @JsonCreator
//    public AlertTagConfig1(@JsonProperty("tagValues") List<String> tagValues, @JsonProperty("tagKey") String tagKey) {
//        this(FilterType.INCLUDE, tagValues, tagKey);
//    }

    @JsonCreator
    public AlertTagConfig1(@JsonProperty("filterType") FilterType filterType, @JsonProperty("tagValues") List<String> tagValues, @JsonProperty("tagKey") String tagKey) {
//        this(LogicType.ALL, filterType, tagValues, tagKey);
        this.filterType = filterType;
        this.tagValues = tagValues;
        this.tagKey = tagKey;
    }

    @JsonCreator
    public AlertTagConfig1(@JsonProperty("logicType") LogicType logicType, @JsonProperty("filterType") FilterType filterType, @JsonProperty("tagValues") List<String> tagValues, @JsonProperty("tagKey") String tagKey) {
        this.logicType = logicType;
        this.filterType = filterType;
        this.tagValues = tagValues;
        this.tagKey = tagKey;
    }


//    public static AlertTagConfig1 of(@JsonProperty("tagKey") String tagKey, @JsonProperty("tagValues") List<String> tagValues) {
//        return of(tagKey, tagValues, FilterType.INCLUDE);
//    }


//    public static AlertTagConfig1 of(@JsonProperty("tagKey") String tagKey, @JsonProperty("tagValues") List<String> tagValues, @JsonProperty("filterType") FilterType filterType) {
////        this(tagKey, tagValues, filterType, LogicType.ALL);
//        AlertTagConfig1 alertConfig = new AlertTagConfig1();
//        alertConfig.tagKey = tagKey;
//        alertConfig.tagValues = tagValues;
//        alertConfig.filterType = filterType;
//        return alertConfig;
//    }

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
