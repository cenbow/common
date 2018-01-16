package kelly.monitor.core;

public class GaugeKey extends MetricKey {

    protected boolean delta = false;
    protected boolean keep = false;

    public GaugeKey(String name) {
        super(name);
    }

    @Override
    public GaugeKey tag(String key, String value) {
        return (GaugeKey) super.tag(key, value);
    }

    public GaugeKey delta() {
        this.delta = true;
        return this;
    }

    public GaugeKey keep() {
        this.keep = true;
        return this;
    }

    public void call(final Gauge gauge) {

        Metrics.INSTANCE.register(this, delta ? new DeltaGauge(gauge, keep) : new com.codahale.metrics.Gauge<Double>() {
            @Override
            public Double getValue() {
                return gauge.getValue();
            }
        });
    }
}