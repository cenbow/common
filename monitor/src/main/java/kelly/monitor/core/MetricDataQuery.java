package kelly.monitor.core;


import kelly.monitor.common.AggregatorType;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;

import java.util.Set;


public class MetricDataQuery extends DefaultQuery {

    private AggregatorType aggregator2;

    private AggregatorType downSampler2;

    private int limit;

    private OrderBy orderBy;

    private String limitNTag;

    private LimitType limitType;

    private MetricType metricType;

    private boolean needArchive = true;

    private Set<String> queryTags;

    private ChartType chartType;

    private Set<String> aggTags;

    /**
     * 对于某个tag，用户自定义条件 各个元素使用-分割
     * AVG|MIN_1,>,100
     */
    private String customExpression;

    private String customExpressionTagKey;

    private Set<ValueType> concernedValueTypes;

    private boolean needPreAgg;

    public boolean isNeedPreAgg() {
        return needPreAgg;
    }

    public void setNeedPreAgg(boolean needPreAgg) {
        this.needPreAgg = needPreAgg;
    }

    public AggregatorType getAggregator2() {
        return aggregator2;
    }

    public void setAggregator2(AggregatorType aggregator2) {
        this.aggregator2 = aggregator2;
    }

    public AggregatorType getDownSampler2() {
        return downSampler2;
    }

    public void setDownSampler2(AggregatorType downSampler2) {
        this.downSampler2 = downSampler2;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public String getLimitNTag() {
        return limitNTag;
    }

    public void setLimitNTag(String limitNTag) {
        this.limitNTag = limitNTag;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getAppCode() {
        return this.getTags().get(TagUtil.TAG_NAME_APP);
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public Set<String> getQueryTags() {
        return queryTags;
    }

    public void setQueryTags(Set<String> queryTags) {
        this.queryTags = queryTags;
    }

    public LimitType getLimitType() {
        return limitType;
    }

    public void setLimitType(LimitType limitType) {
        this.limitType = limitType;
    }

    public boolean isNeedArchive() {
        return needArchive;
    }

    public void setNeedArchive(boolean needArchive) {
        this.needArchive = needArchive;
    }

    public String getCustomExpression() {
        return customExpression;
    }

    public void setCustomExpression(String customExpression) {
        this.customExpression = customExpression;
    }

    public String getCustomExpressionTagKey() {
        return customExpressionTagKey;
    }

    public Set<ValueType> getConcernedValueTypes() {
        return concernedValueTypes;
    }

    public void setConcernedValueTypes(Set<ValueType> concernedValueTypes) {
        this.concernedValueTypes = concernedValueTypes;
    }

    public void setCustomExpressionTagKey(String customExpressionTagKey) {
        this.customExpressionTagKey = customExpressionTagKey;
    }

    public Set<String> getAggTags() {
        return aggTags;
    }

    public void setAggTags(Set<String> aggTags) {
        this.aggTags = aggTags;
    }

    public static enum OrderBy {
        desc, asc
    }

    public static enum LimitType {
        top, bottom, delta
    }

    public static enum ChartType {
        list, detail
    }
}
