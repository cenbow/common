package kelly.monitor.core;

/**
 * 一个数据点
 */
public interface DataPoint {

    /**
     * UNIX时间戳
     * 
     * @return
     */
    long timestamp();

    /**
     * 数据值
     * 
     * @return
     */
    float value();
}
