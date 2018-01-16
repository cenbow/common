package kelly.monitor.core;

public abstract class KeyWrapper<T> extends MetricKey {

    public KeyWrapper(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public KeyWrapper<T> tag(String key, String value) {
        return (KeyWrapper<T>) super.tag(key, value);
    }

    public abstract T get();
}