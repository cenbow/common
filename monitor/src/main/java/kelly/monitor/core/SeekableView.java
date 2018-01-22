package kelly.monitor.core;

import java.util.Iterator;

/**
 * Provides a <em>zero-copy view</em> to iterate through data points.
 */
public interface SeekableView extends Iterator<DataPoint> {

    /**
     * Advances the iterator to the given point in time.
     * <p>
     * This allows the iterator to skip all the data points that are strictly
     * before the given timestamp.
     * 
     * @param timestamp A strictly positive 32 bit UNIX timestamp (in seconds).
     * @throws IllegalArgumentException if the timestamp is zero, or negative,
     * or doesn't fit on 32 bits (think "unsigned int" -- yay Java!).
     */
    void seek(long timestamp);
}
