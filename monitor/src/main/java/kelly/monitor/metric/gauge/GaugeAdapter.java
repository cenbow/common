package kelly.monitor.metric.gauge;

import kelly.monitor.metric.Gauge;

/**
 * Created by kelly.li on 18/1/21.
 */
public class GaugeAdapter implements Gauge {

    protected final com.codahale.metrics.Gauge<Double> _gauge;

    public GaugeAdapter(double value) {
        _gauge = new com.codahale.metrics.Gauge() {
            @Override
            public Double getValue() {
                return value;
            }
        };
    }

    public GaugeAdapter(com.codahale.metrics.Gauge<Double> gauge) {
        _gauge = gauge;
    }

    @Override
    public double getValue() {
        return _gauge.getValue();
    }

    @Override
    public Object[] values() {
        return new Object[]{getValue()};
    }
}
