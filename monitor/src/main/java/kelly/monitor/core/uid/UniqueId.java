package kelly.monitor.core.uid;


/**
 * Represents a table of Unique IDs, manages the lookup and creation of IDs.
 * 
 * For efficiency, various kinds of "names" need to be mapped to small, unique
 * IDs. For instance, we give a unique ID to each metric name, to each tag name,
 * to each tag value.
 * <p>
 * An instance of this class handles the unique IDs for one kind of ID. For
 * example:
 * 
 * <pre>
 *   UniqueId metric_names = ...;
 *   byte[] id = metric_names.get("sys.net.rx_bytes");
 * </pre>
 * 
 * IDs are looked up in HBase and cached forever in memory (since they're
 * immutable). IDs are encoded on a fixed number of bytes, which is
 * implementation dependent.
 */
public interface UniqueId {

    /**
     * Finds the name associated with a given ID.
     * 
     * @param id The ID associated with that name.
     * @see #getId(String)
     * @see #getOrCreateId(String)
     * @throws NoSuchUniqueId if the given ID is not assigned.
     * @throws IllegalArgumentException if the ID given in argument is encoded
     * on the wrong number of bytes.
     */
    String getName(byte[] id) throws NoSuchUniqueId;

    String getName(final int id) throws NoSuchUniqueId;

    /**
     * Finds the ID associated with a given name.
     * <p>
     * The length of the byte array is fixed in advance by the implementation.
     * 
     * @param name The name to lookup in the table.
     * @see #getName(byte[])
     * @return A non-null, non-empty {@code byte[]} array.
     * @throws NoSuchUniqueName if the name requested doesn't have an ID
     * assigned.
     * @throws IllegalStateException if the ID found in HBase is encoded on the
     * wrong number of bytes.
     */
    byte[] getId(String name) throws NoSuchUniqueName;

    int getId1(String name) throws NoSuchUniqueName;
    /**
     * Finds the ID associated with a given name or creates it.
     * <p>
     * The length of the byte array is fixed in advance by the implementation.
     * 
     * @param name The name to lookup in the table or to assign an ID to.
     * @throws IllegalStateException if all possible IDs are already assigned.
     * @throws IllegalStateException if the ID found in HBase is encoded on the
     * wrong number of bytes.
     */
    byte[] getOrCreateId(String name) throws IllegalStateException;

    int getOrCreateId1(String name) throws IllegalStateException;

    /**
     * Attempts to find suggestions of names given a search term.
     * 
     * @param search The search term (possibly empty).
     * @return A list of known valid names that have UIDs that sort of match the
     * search term. If the search term is empty, returns the first few terms.
     */
    // TODO DAtrie
    // List<String> suggest(String search);
}
