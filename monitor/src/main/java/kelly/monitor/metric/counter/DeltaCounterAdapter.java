package kelly.monitor.metric.counter;

/**
 * Created by kelly.li on 18/1/21.
 */
public class DeltaCounterAdapter extends CounterAdapter {

    public DeltaCounterAdapter(boolean keep) {
        super(new DeltaCounter(keep));
    }

}
