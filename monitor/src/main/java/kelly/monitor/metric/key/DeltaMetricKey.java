package kelly.monitor.metric.key;

public  class DeltaMetricKey extends MetricKey {

    /**
     * 只记录变化量
     */
    protected boolean delta = false;
    /**
     * 当两次检查数据一致时，维持变化量
     */
    protected boolean keep = false;

    public DeltaMetricKey(String name) {
        super(name);
    }


    public DeltaMetricKey delta() {
        this.delta = true;
        return this;
    }


    public DeltaMetricKey keep() {
        this.keep = true;
        return this;
    }


    public boolean isDelta() {
        return delta;
    }


    public boolean isKeep() {
        return keep;
    }

}
