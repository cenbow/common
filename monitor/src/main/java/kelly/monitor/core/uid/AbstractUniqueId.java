package kelly.monitor.core.uid;

import com.google.common.base.CharMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

abstract class AbstractUniqueId implements UniqueId {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Number of bytes on which each ID is encoded.
     */
    protected static final short ID_WIDTH = 3;

    /**
     * How many time do we try to assign an ID before giving up.
     */
    protected static final short MAX_ATTEMPTS_ASSIGN_ID = 3;

    @Override
    public int getId1(String name) throws NoSuchUniqueName {
        return fromBytes(getId(name));
    }

    @Override
    public byte[] getId(String name) throws NoSuchUniqueName {
        // valid name?
        if (!isIdentifier(name)) {
            throw new IllegalArgumentException("Invalid identifier, name: " + name);
        }
        Integer _id = getNullableId(name);
        if (_id == null) {
            throw new NoSuchUniqueName(name);
        }
        byte[] id = toBytes(_id);
        if (id.length != ID_WIDTH) {
            throw new IllegalStateException("Found id.length = " + id.length + " which is != " + ID_WIDTH);
        }
        return id;
    }

    @Override
    public String getName(int id) throws NoSuchUniqueId {
        return getName(toBytes(id));
    }

    @Override
    public String getName(byte[] _id) throws NoSuchUniqueId {
        if (_id.length != ID_WIDTH) {
            throw new IllegalArgumentException("Wrong id.length = " + _id.length + " which is != " + ID_WIDTH);
        }
        int id = fromBytes(_id);
        String name = getNullableName(id);
        if (name == null) {
            throw new NoSuchUniqueId(_id);
        }
        return name;
    }

    @Override
    public byte[] getOrCreateId(final String name) throws IllegalStateException {
        short attempt = MAX_ATTEMPTS_ASSIGN_ID;
        while (attempt-- > 0) {
            try {
                return getId(name);
            } catch (NoSuchUniqueName e) {
                if (logger.isDebugEnabled()) {
                    logger.info("Creating an ID for name='{}'", name);
                }
            }
            int id = createId(name);
            if (id <= 0) {
                continue;
            }
            if ((id & 0xFF000000) != 0) {
                throw new IllegalStateException("Too much IDs than expected");
            }
            return toBytes(id);
        }
        logger.error("Failed to assign an ID for name='{}'", name);
        throw new IllegalStateException("Should never happen!");
    }

    @Override
    public int getOrCreateId1(final String name) throws IllegalStateException {
        short attempt = MAX_ATTEMPTS_ASSIGN_ID;
        while (attempt-- > 0) {
            try {
                return getId1(name);
            } catch (NoSuchUniqueName e) {
                if (logger.isDebugEnabled()) {
                    logger.info("Creating an ID for name='{}'", name);
                }
            }
            int id = createId(name);
            if (id <= 0) {
                continue;
            }
            if ((id & 0xFF000000) != 0) {
                throw new IllegalStateException("Too much IDs than expected");
            }
            return id;
        }
        logger.error("Failed to assign an ID for name='{}'", name);
        throw new IllegalStateException("Should never happen!");
    }

    private static Pattern identifier = Pattern.compile("^[_0-9a-zA-Z][0-9a-zA-Z_\\-\\.]*$");

    private static final int NAME_MAX_LENGTH = 150;

    private static final CharMatcher charMatcher = CharMatcher.anyOf("-_.").or(CharMatcher.JAVA_LETTER_OR_DIGIT);

    protected boolean isIdentifier(String name) {
        return name != null && !name.isEmpty() && name.length() < NAME_MAX_LENGTH && charMatcher.matchesAllOf(name);
        //return s != null && !s.isEmpty() && identifier.matcher(s).find();
    }

    /**
     * @param name
     * @return 0 if failed, positive if succeed.
     */
    protected abstract int createId(String name);

    protected abstract Integer getNullableId(String name);

    protected abstract String getNullableName(Integer id);

    static byte[] toBytes(final int i) {
        byte[] bs = new byte[3];
        // aaaaaaaa|bbbbbbbb|cccccccc|dddddddd
        bs[0] = (byte) ((i >> 16) & 0xFF); // bbbbbbbb
        bs[1] = (byte) ((i >> 8) & 0xFF); // cccccccc
        bs[2] = (byte) ((i >> 0) & 0xFF); // dddddddd

        return bs;
    }

    static int fromBytes(final byte[] bs) {
        // bbbbbbbb|cccccccc|dddddddd
        int value = 0;
        value += (bs[0] & 0x000000FF) << 16;
        value += (bs[1] & 0x000000FF) << 8;
        value += (bs[2] & 0x000000FF);
        return value;
    }
}
