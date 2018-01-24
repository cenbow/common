package kelly.monitor.core;


public interface Constant {

    static final byte[] FAMILY = { 'k' };

    /** 指标名 */
    static final short METRIC_WIDTH = 3;
    /** 指标类型 */
    static final short METRIC_TYPE_WIDTH = 1;
    /** 指标时间 */
    static final short TIMESTAMP_WIDTH = 4;
    /** 标签名 */
    static final short TAG_NAME_WIDTH = 3;
    /** 标签值 */
    static final short TAG_VALUE_WIDTH = 3;
    /** 单个标签占用字节 */
    static final short TAG_WIDTH = TAG_NAME_WIDTH + TAG_VALUE_WIDTH;
    /** 单个数值占用字节 */
    static final short ONE_VALUE_WIDTH = 4;
    /** 单个Qualifier占用字节 */
    static final short QUALIFIER_WITDH = 2;

    static final short MIN_TAG_COUNT = 1;
    static final short MAX_TAG_COUNT = 8;

    static final short MAX_TIMESPAN = 3600;
}
