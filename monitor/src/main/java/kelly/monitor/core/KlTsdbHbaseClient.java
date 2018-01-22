package kelly.monitor.core;

import org.hbase.async.HBaseClient;

/**
 * 由于HBaseClient不能通过注入设置 flushInterval和incrementBufferSize则通过改包装类注入
 */
public class KlTsdbHbaseClient {

    private final HBaseClient hBaseClient;

    public KlTsdbHbaseClient(final String quorumSpec, final String basePath) {
        this(quorumSpec, basePath, 0, 0);
    }

    public KlTsdbHbaseClient(final String quorumSpec, final String basePath, int flushInterval, int incrementBufferSize) {
        this.hBaseClient = new HBaseClient(quorumSpec, basePath);
        if (flushInterval != 0) {
            hBaseClient.setFlushInterval((short) flushInterval);
        }
        if (incrementBufferSize != 0) {
            hBaseClient.setIncrementBufferSize(incrementBufferSize);
        }
    }

    public KlTsdbHbaseClient(final String quorumSpec, final String basePath, int flushInterval) {
        this(quorumSpec, basePath, flushInterval, 0);
    }

    public HBaseClient gethBaseClient() {
        return hBaseClient;
    }
}
