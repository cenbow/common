package kelly.monitor.metric.meter;


import kelly.monitor.metric.Meter;

public class MeterAdapter implements Meter {

    private final com.codahale.metrics.Meter _meter;

    public MeterAdapter() {
        this._meter = new com.codahale.metrics.Meter();
    }

    public MeterAdapter(com.codahale.metrics.Meter meter) {
        this._meter = meter;
    }

    @Override
    public void mark() {
        mark(1);
    }

    @Override
    public void mark(long n) {
        _meter.mark(n);
    }

    @Override
    public long getCount() {
        return _meter.getCount();
    }

    @Override
    public Object[] values() {
        return new Object[]{
                (float) _meter.getMeanRate(),
                (float) _meter.getOneMinuteRate(),
                (float) _meter.getFiveMinuteRate(),
                (float) _meter.getFifteenMinuteRate()};
    }
}