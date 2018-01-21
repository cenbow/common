package kelly.monitor.core;

public abstract class DeltaKeyWrapper<T> extends KeyWrapper<T> {

    protected boolean delta = false;
    protected boolean keep = false;

    public DeltaKeyWrapper(String name) {
        super(name);
    }

    public DeltaKeyWrapper<T> tag(String key, String value) {
        return (DeltaKeyWrapper<T>) super.tag(key, value);
    }

    /**
     * 只记录变化量
     */
    public DeltaKeyWrapper<T> delta() {
        this.delta = true;
        return this;
    }

    /**
     * 当两次检查数据一致时，维持变化量
     */
    public DeltaKeyWrapper<T> keep() {
        this.keep = true;
        return this;
    }
}
