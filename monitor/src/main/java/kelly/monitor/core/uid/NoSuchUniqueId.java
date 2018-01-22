package kelly.monitor.core.uid;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Exception used when a Unique ID can't be found.
 * 
 * @see UniqueId
 */
public class NoSuchUniqueId extends NoSuchElementException {

    private static final long serialVersionUID = 7288259022206620483L;

    /** The ID that couldn't be found. */
    private final byte[] id;

    /**
     * Constructor.
     * 
     * @param kind The kind of unique ID that triggered the exception.
     * @param id The ID that couldn't be found.
     */
    public NoSuchUniqueId(final byte[] id) {
        super("No such unique ID : " + Arrays.toString(id));
        this.id = id;
    }

    /** Returns the unique ID that couldn't be found. */
    public byte[] id() {
        return id;
    }
}
