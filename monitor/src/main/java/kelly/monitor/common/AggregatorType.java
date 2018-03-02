package kelly.monitor.common;

/**
 * 预聚合函数类型
 * 只能增加 不能减少
 */

public enum AggregatorType {

    SUM("总和"),
    MIN("最小值"),
    MAX("最大值"),
    AVG("平均值"),
    STDDEV("标准差");

    private String text;

    private AggregatorType(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }


}