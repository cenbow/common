package kelly.monitor.metric;

/**
 * Created by kelly.li on 18/1/20.
 */
public interface Counter extends Metric{

    void inc();

    void inc(long n);

    void dec();

    void dec(long n);

    long getCount();
}
