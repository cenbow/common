package kelly.monitor.core;


import kelly.monitor.common.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractSeekableView implements SeekableView {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractSeekableView(ValueType valueType) {
        this.valueType = valueType;
    }

    protected final ValueType valueType;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
