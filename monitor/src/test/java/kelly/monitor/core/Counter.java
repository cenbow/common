package kelly.monitor.core;

public interface Counter {

    void inc();

    void inc(long n);

    void dec();

    void dec(long n);

    long getCount();
}
