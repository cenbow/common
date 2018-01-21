package kelly.monitor.metric;

/**
 * Created by kelly.li on 18/1/20.
 */
public interface Meter extends Metric {

    void mark();

    void mark(long n);

    long getCount();
}
