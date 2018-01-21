package kelly.monitor.core;

public interface Meter {

    void mark();

    void mark(long n);

    long getCount();
}
