package kelly.monitor.metric.counter;


import kelly.monitor.metric.Counter;

/**
 * Created by kelly.li on 18/1/20.
 */
public class CounterAdapter implements Counter {

    protected final com.codahale.metrics.Counter _counter;

    public CounterAdapter() {
        this._counter = new com.codahale.metrics.Counter();
    }

    public CounterAdapter(com.codahale.metrics.Counter counter) {
        this._counter = counter;
    }

    @Override
    public void inc() {
        inc(1);
    }

    @Override
    public void inc(long n) {
        _counter.inc(n);
    }

    @Override
    public void dec() {
        dec(1);
    }

    @Override
    public void dec(long n) {
        _counter.dec(n);
    }

    @Override
    public long getCount() {
        return _counter.getCount();
    }

    public Object[] values() {
        return new Object[]{(float) getCount()};
    }
}