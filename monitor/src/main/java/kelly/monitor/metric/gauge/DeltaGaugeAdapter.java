package kelly.monitor.metric.gauge;

/**
 * Created by kelly.li on 18/1/21.
 */
public class DeltaGaugeAdapter extends GaugeAdapter {

    public DeltaGaugeAdapter(double value, boolean keep) {
        super(new DeltaGauge(value, keep));
    }
}
