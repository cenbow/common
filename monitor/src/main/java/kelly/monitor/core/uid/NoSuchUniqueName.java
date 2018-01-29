package kelly.monitor.core.uid;

import java.util.NoSuchElementException;

/**
 * Exception used when a name's Unique ID can't be found.
 * 
 * @see UniqueId
 */
public class NoSuchUniqueName extends NoSuchElementException {

    private static final long serialVersionUID = -7342635873866012562L;

    /** The name that couldn't be found. */
    private final String name;

    /**
     * Constructor.
     *
     * @param name The name that couldn't be found.
     */
    public NoSuchUniqueName(final String name) {
        super("No such name: '" + name + "'");
        this.name = name;
    }

    /** Returns the name for which the unique ID couldn't be found. */
    public String name() {
        return name;
    }
}
