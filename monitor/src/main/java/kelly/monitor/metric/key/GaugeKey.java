package kelly.monitor.metric.key;

import kelly.monitor.metric.Gauge;
import kelly.monitor.metric.Metrics;
import kelly.monitor.metric.gauge.DeltaGaugeAdapter;
import kelly.monitor.metric.gauge.GaugeAdapter;

/**
 * Created by kelly.li on 18/1/21.
 */
public class GaugeKey extends DeltaMetricKey {

    public GaugeKey(String name) {
        super(name);
    }

    public GaugeKey delta() {
        super.delta();
        return this;
    }

    public GaugeKey keep() {
        super.keep();
        return this;
    }

    public Gauge value(double value) {
        Gauge gauge = delta ? new DeltaGaugeAdapter(value, keep) : new GaugeAdapter(value);
        Metrics.INSTANCE.set(this, gauge);
        return gauge;
    }


}
