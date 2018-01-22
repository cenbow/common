package kelly.monitor.core;

/**
 * 聚合计算插值类别
 */
public enum Interpolation {

    LERP,   /* Regular linear interpolation */
    ZIM,    /* Returns 0 when a data point is missing */
    MAX,    /* Returns the <type>.MaxValue when a data point is missing */
    MIN     /* Returns the <type>.MinValue when a data point is missing */
}
