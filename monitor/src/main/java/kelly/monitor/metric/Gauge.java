package kelly.monitor.metric;


/**
 * Created by kelly.li on 18/1/20.
 */
public interface Gauge extends Metric {

    double getValue();
}
