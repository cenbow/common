package kelly.monitor.core;


import kelly.monitor.core.uid.NoSuchUniqueId;
import kelly.monitor.core.uid.NoSuchUniqueName;
import kelly.monitor.core.uid.UniqueId;
import org.hbase.async.Bytes;

import java.util.*;

/** Helper functions to deal with tags. */
public final class Tags implements Constant {

    /**
     * 从rowkey中把tag的key-value解析出来
     * @param rowKey
     * @return
     * @throws NoSuchUniqueId
     */
    public static Map<byte[], byte[]> getTagsByteMapFromRowKey(final byte[] rowKey) throws NoSuchUniqueId {

        Map<byte[], byte[]> result = new Bytes.ByteMap<byte[]>();

        for (short pos = METRIC_WIDTH + TIMESTAMP_WIDTH; pos < rowKey.length - METRIC_TYPE_WIDTH;) {
            byte[] tag_name = Arrays.copyOfRange(rowKey, pos, pos + TAG_NAME_WIDTH);
            pos += TAG_NAME_WIDTH;
            byte[] tag_value = Arrays.copyOfRange(rowKey, pos, pos + TAG_VALUE_WIDTH);
            pos += TAG_VALUE_WIDTH;
            result.put(tag_name, tag_value);
        }
        return result;
    }

    /**
     * Returns the tags stored in the given row key.
     * 
     * @param uniqueId Unique ID lookups.
     * @param key The row key from which to extract the tags.
     * @return A map of tag names (keys), tag values (values).
     * @throws NoSuchUniqueId if the row key contained an invalid ID (unlikely).
     */
    static Map<String, String> getTags(final UniqueId uniqueId, final byte[] key) throws NoSuchUniqueId {

        final byte[] tmp_name = new byte[TAG_NAME_WIDTH];
        final byte[] tmp_value = new byte[TAG_VALUE_WIDTH];

        final short metric_ts_bytes = METRIC_WIDTH + TIMESTAMP_WIDTH;
        final int tags_data_range = key.length - metric_ts_bytes - METRIC_TYPE_WIDTH;

        final Map<String, String> result = new HashMap<String, String>(tags_data_range / TAG_WIDTH);

        for (short pos = metric_ts_bytes; pos < key.length - METRIC_TYPE_WIDTH; pos += TAG_WIDTH) {
            System.arraycopy(key, pos, tmp_name, 0, TAG_NAME_WIDTH);
            System.arraycopy(key, pos + TAG_NAME_WIDTH, tmp_value, 0, TAG_VALUE_WIDTH);
            result.put(uniqueId.getName(tmp_name), uniqueId.getName(tmp_value));
        }

        return result;
    }

    /**
     * Resolves all the tags (name=value) into the a sorted byte arrays. This
     * 
     * @param uniqueId UniqueId lookups.
     * @param tags The tags to resolve.
     * @return an array of sorted tags (tag id, tag name).
     * @throws NoSuchUniqueName if one of the elements in the map contained an
     * unknown tag name or tag value.
     */
    public static List<byte[]> resolveAll(final UniqueId uniqueId, final Map<String, String> tags)
            throws NoSuchUniqueName {
        return resolveAllInternal(uniqueId, tags, false);
    }

    /**
     * Resolves (and creates, if necessary) all the tags (name=value) into the a
     * sorted byte arrays.
     *
     * @param tags The tags to resolve. If a new tag name or tag value is seen,
     * it will be assigned an ID.
     * @return an array of sorted tags (tag id, tag name).
     */
    public static List<byte[]> resolveOrCreateAll(final UniqueId uniqueId, final Map<String, String> tags) {
        return resolveAllInternal(uniqueId, tags, true);
    }

    private static List<byte[]> resolveAllInternal(final UniqueId uniqueId, final Map<String, String> tags,
            final boolean create) throws NoSuchUniqueName {

        final ArrayList<byte[]> tag_byte_list = new ArrayList<byte[]>(tags.size());

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            byte[] t_id = (create ? uniqueId.getOrCreateId(key) : uniqueId.getId(key));
            byte[] t_value = (create ? uniqueId.getOrCreateId(value) : uniqueId.getId(value));
            byte[] tag = new byte[t_id.length + t_value.length];
            System.arraycopy(t_id, 0, tag, 0, t_id.length);
            System.arraycopy(t_value, 0, tag, t_id.length, t_value.length);
            tag_byte_list.add(tag);
        }

        Collections.sort(tag_byte_list, Bytes.MEMCMP);

        return tag_byte_list;
    }
}
