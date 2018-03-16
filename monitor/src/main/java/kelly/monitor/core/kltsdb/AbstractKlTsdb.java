package kelly.monitor.core.kltsdb;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.stumbleupon.async.Deferred;
import kelly.monitor.common.AggregatorType;
import kelly.monitor.common.MetricType;
import kelly.monitor.core.*;
import kelly.monitor.core.uid.UniqueId;
import org.apache.commons.lang.StringUtils;
import org.hbase.async.*;
import org.hbase.async.Bytes.ByteMap;
import org.hbase.async.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;


public abstract class AbstractKlTsdb implements Constant, KlTsdb, QueryRunner {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final List<DataPoints> NO_RESULT = Collections.emptyList();

    /**
     * 监控数据存储访问
     */
    protected final HBaseClient client;

    /**
     * 元数据访问, 指标名/标签名/标签值
     */
    protected final UniqueId uniqueId;

    /**
     * 监控数据表名
     */
    protected final byte[] table;

    protected final int qualifierWidth;

    protected final int rowTimeSpan;

    /**
     * 创建KlTsdb.
     *
     * @param client   监控数据HBase客戶端
     * @param table    监控数据表名
     * @param uniqueId 元信息ID
     */
    public AbstractKlTsdb(HBaseClient client, String table, UniqueId uniqueId) {
        checkInitArgument(client, table, uniqueId);
        this.client = client;
        this.table = table.getBytes();
        this.uniqueId = uniqueId;
        qualifierWidth = QUALIFIER_WITDH;
        rowTimeSpan = MAX_TIMESPAN;
    }

    public AbstractKlTsdb(HBaseClient client, String table, UniqueId uniqueId, int qualifierWidth, int rowTimeSpan) {
        checkInitArgument(client, table, uniqueId);
        this.client = client;
        this.table = table.getBytes();
        this.uniqueId = uniqueId;
        this.qualifierWidth = qualifierWidth;
        this.rowTimeSpan = rowTimeSpan;
    }

    private void checkInitArgument(HBaseClient client, String table, UniqueId uniqueId) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(table), "table is empty");
        Preconditions.checkNotNull(client, "client is null");
        Preconditions.checkNotNull(uniqueId, "uniqueId is null");
    }

    @PostConstruct
    private void init() {
        this.client.setFlushInterval((short) 3000);
        this.client.setIncrementBufferSize(0x20000);
    }
//appcode_metric tagNames tagValues
    @Override
    public Deferred<Object> addPoints(final String metricName, final MetricType type, final long timestamp, final Float[] values, final Map<String, String> tags) {
        if (logger.isDebugEnabled()) {
            logger.debug("### metric={}, type={}, timestamp={}, values={}, tags={}", metricName, type, timestamp, values, tags);
        }
        validateBeforeAddPoint(metricName, type, values, tags);
//        Timer.Context context = Metrics.timer("KlTsdbAddPointsTimer").get().time();
        try {
            byte[] metric_byte = uniqueId.getOrCreateId(metricName);
            List<byte[]> tag_byte_list = Tags.resolveOrCreateAll(uniqueId, tags);
            byte[] value_byte = buildValueBytes(values);
            return addPointsInternal(metric_byte, (byte) type.ordinal(), timestamp, value_byte, tag_byte_list);
        } catch (Exception e) {
            logger.warn("save point to hbase error! metricName={}, tags={}, type={}, timestamp={}",
                    metricName, tags, type, timestamp, e);
            return Deferred.fromError(e);
        } finally {
//            context.stop();
        }
    }

    protected byte[] buildValueBytes(Float[] values) {
        byte[] value_byte = new byte[values.length * ONE_VALUE_WIDTH];
        int pos = 0;
        for (float value : values) {
            System.arraycopy(Bytes.fromInt(Float.floatToRawIntBits(value)), 0, value_byte, pos, ONE_VALUE_WIDTH);
            pos += ONE_VALUE_WIDTH;
        }
        return value_byte;
    }

    protected void validateBeforeAddPoint(String metric, MetricType type, Float[] values, Map<String, String> tags) {
        if (metric == null || metric.isEmpty()) {
            throw new IllegalArgumentException("metrics name required.");
        }
        if (type == null) {
            throw new IllegalArgumentException("metrics type required.");
        }
        if (tags.size() < MIN_TAG_COUNT) {
            throw new IllegalArgumentException("tags required.");
        }
        if (tags.size() > MAX_TAG_COUNT) {
            throw new IllegalArgumentException("too much tags, must less than " + MAX_TAG_COUNT);
        }
    }

    protected Deferred<Object> addPointsInternal(final byte[] metric, final byte type, final long timestamp,
                                                 final byte[] values, final List<byte[]> tags) {
        final long unixTime = timestamp / 1000;
        final short qualifier = (short) (unixTime % rowTimeSpan);
        final long baseTime = unixTime - qualifier; // 1 rowTimeSpan
        byte[] key = RowKey.rowKey(metric, (int) baseTime, tags, type);
        PutRequest request = new PutRequest(table, key, FAMILY, Bytes.fromShort(qualifier), values);
        request.setDurable(true);//打开WAL，防止丢数据
        return client.put(request);
    }

    /**
     * Runs this query.
     *
     * @return The data points matched by this query.
     * <p/>
     * Each element in the non-{@code null} but possibly empty array returned
     * corresponds to one time series for which some data points have been
     * matched by the query.
     * @throws org.hbase.async.HBaseException if there was a problem communicating with HBase to
     *                                        perform the search.
     */
    @Override
    public List<DataPoints> run(Query query) throws Exception {
        String metric = query.getMetric();
        Date startTime = query.getStartTime();
        Date endTime = query.getEndTime();
        Map<String, String> tags = query.getTags();
        AggregatorType aggregator = query.getAggregator();
        AggregatorType downsampler = query.getDownSampler();
        int interval = query.getSampleInterval();
        logger.info("startTime={}, endTime={}, metric={}, tags={}, aggregator={}, downSampler={}, sampleInterval={}", startTime, endTime, metric, tags,
                aggregator, downsampler, interval);

//        final Timer.Context context = Metrics.timer("KlTsdbRunQueryTimer").get().time();
        try {
            final byte[] metric_byte = uniqueId.getId(metric);
            final List<byte[]> tagKeyByteList = new ArrayList<byte[]>();
            final ByteMap<byte[][]> tagKeyValueByteMap = new ByteMap<byte[][]>();

            findGroupBys(uniqueId, tags, tagKeyByteList, tagKeyValueByteMap);
            // sorted tags byte list
            final List<byte[]> tagKeyValueByteList = Tags.resolveAll(uniqueId, tags);

            long unixStartSeconds = query.getStartTime().getTime() / 1000;
            long unixEndSeconds = query.getEndTime().getTime() / 1000;

            final Scanner scanner = getScanner(metric_byte, unixStartSeconds, unixEndSeconds, interval);
            if (tags.size() > 0 || !tagKeyByteList.isEmpty()) {
                // tag filter
                scanner.setKeyRegexp(createRegexFilter(tagKeyValueByteList, tagKeyByteList, tagKeyValueByteMap), Charsets.ISO_8859_1);
            }
            final Map<byte[], Span> spans = findSpans(query, scanner, metric_byte);
            return groupByAndAggregate(query, spans, tagKeyByteList, unixStartSeconds - interval, unixEndSeconds + 2 * interval);
        } finally {
//            context.stop();
        }
    }

    /**
     * <pre>
     * host:127.0.0.1           => tags={host:127.0.0.1}
     * host:*                   => tags={}, group_bys={host}
     * host:127.0.0.1|127.0.0.2 => tags={}, group_bys={host}, group_by_values={host:[127.0.0.1, 127.0.0.2]}
     * </pre>
     */
    private void findGroupBys(final UniqueId uniqueId, final Map<String, String> tags, List<byte[]> tagKeyByteList, ByteMap<byte[][]> tagKeyValueByteMap) {

        Iterator<Entry<String, String>> iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> e = iterator.next();
            final String key = e.getKey();
            final String value = e.getValue();

            if (!value.equals(TagUtil.TAG_VALUE_ALL) && value.indexOf(TagUtil.TAG_VALUE_SPLIT) < 0) {
                // host:127.0.0.1 => scanner filter
                continue;
            }

            // host:*
            // host:127.0.0.1|127.0.0.2
            // => group_bys
            byte[] tag_key_id = uniqueId.getId(key);
            tagKeyByteList.add(tag_key_id);
            iterator.remove();

            if (value.equals(TagUtil.TAG_VALUE_ALL)) {
                continue;
            }

            // host:127.0.0.1|127.0.0.2
            // => group_by_values
            String[] values = StringUtils.split(value, TagUtil.TAG_VALUE_SPLIT);
            byte[][] tag_values = new byte[values.length][TAG_VALUE_WIDTH];
            for (int i = 0; i < values.length; i++) {
                byte[] value_byte = uniqueId.getId(values[i]);
                System.arraycopy(value_byte, 0, tag_values[i], 0, value_byte.length);
            }
            tagKeyValueByteMap.put(tag_key_id, tag_values);
        }
    }

    /**
     * Creates the {@link org.hbase.async.Scanner} to use for this query.
     */
    protected Scanner getScanner(final byte[] metric, long unixStartSeconds, long unixEndSeconds, int interval) throws HBaseException {
        final int scan_start = (int) ((unixStartSeconds - rowTimeSpan - interval) & 0x00000000FFFFFFFFL);
        final int scan_end = (int) ((unixEndSeconds + rowTimeSpan + 1 + interval) & 0x00000000FFFFFFFFL);

        final short metric_ts_bytes = METRIC_WIDTH + TIMESTAMP_WIDTH;
        final short metric_width = METRIC_WIDTH;

        final byte[] start_row = new byte[metric_ts_bytes];
        final byte[] end_row = new byte[metric_ts_bytes];

        System.arraycopy(metric, 0, start_row, 0, metric_width);
        System.arraycopy(metric, 0, end_row, 0, metric_width);
        Bytes.setInt(start_row, scan_start, metric_width);
        Bytes.setInt(end_row, scan_end, metric_width);

        final Scanner scanner = client.newScanner(table);
        scanner.setStartKey(start_row);
        scanner.setStopKey(end_row);
        scanner.setFamily(FAMILY);
        return scanner;
    }

    /**
     * Sets the server-side regexp filter on the scanner. In order to find the
     * rows with the relevant tags, we use a server-side filter that matches a
     * regular expression on the row key.
     */
    private String createRegexFilter(List<byte[]> tagKeyValueByteList, List<byte[]> tagKeyByteList, ByteMap<byte[][]> tagKeyValueByteMap) {
        if (!tagKeyByteList.isEmpty()) {
            Collections.sort(tagKeyByteList, Bytes.MEMCMP);
        }
        /*
         * tags: { 0 0 1 0 0 2 } and { 4 5 6 9 8 7 }, the regexp will be:
         * "^.{7}(?:.{6})*\\Q\000\000\001\000\000\002\\E(?:.{6})*\\Q\004\005\006\011\010\007\\E(?:.{6})*.{1}$"
         */
        // TODO calculate regexp size directly
        final StringBuilder regexp = new StringBuilder();

        // (?s) - we use the DOTALL flag.
        regexp.append("(?s)");
        // ^.{N} - skipping the metric ID and timestamp.
        regexp.append("^.{").append(METRIC_WIDTH + TIMESTAMP_WIDTH).append("}");

        final Iterator<byte[]> tag_i = tagKeyValueByteList.iterator();
        final Iterator<byte[]> tagKeyByte_i = tagKeyByteList.iterator();

        byte[] tag = tag_i.hasNext() ? tag_i.next() : null;
        byte[] group_by = tagKeyByte_i.hasNext() ? tagKeyByte_i.next() : null;

        /*
         * Tags and group_bys are already sorted. We need to put them in the
         * regexp in order by ID, which means we just merge two sorted lists.
         */
        do {
            // Skip any number of tabs.
            regexp.append("(?:.{").append(TAG_WIDTH).append("})*\\Q");

            if (isTagNext(TAG_NAME_WIDTH, tag, group_by)) {
                addId(regexp, tag);
                tag = tag_i.hasNext() ? tag_i.next() : null;
            } else {
                // Add a group_by.
                addId(regexp, group_by);
                final byte[][] value_ids = tagKeyValueByteMap.get(group_by);
                if (value_ids == null) {
                    // We don't want any specific ID...
                    // Any value ID.
                    regexp.append(".{").append(TAG_VALUE_WIDTH).append('}');
                } else {
                    // We want specific IDs. List them: /(AAA|BBB|CCC|..)/
                    regexp.append("(?:");
                    for (final byte[] value_id : value_ids) {
                        regexp.append("\\Q");
                        addId(regexp, value_id);
                        regexp.append('|');
                    }
                    // Replace the pipe of the last iteration.
                    regexp.setCharAt(regexp.length() - 1, ')');
                }
                group_by = tagKeyByte_i.hasNext() ? tagKeyByte_i.next() : null;
            }
            // Stop when they both become null.
        } while (tag != group_by);

        // skip any number of tags before the end.
        regexp.append("(?:.{").append(TAG_WIDTH).append("})*");
        // skip metric type
        regexp.append(".{").append(METRIC_TYPE_WIDTH).append("}$");

        return regexp.toString();
    }


    private Map<byte[], Span> findSpans(Query query, Scanner scanner, byte[] _metric) throws HBaseException {
//        Timer.Context time = Metrics.timer("KlTsdbFindSpansTimer").get().time();
        // row key => spans
        final Map<byte[], Span> spans = new TreeMap<byte[], Span>(new SpanCmp());//自定义比较器，跳过时间的比较，按metirc tags valuetype相同的分组
        ArrayList<ArrayList<KeyValue>> rows;
        int nrows = 0;
        try {
            while ((rows = scanner.nextRows().joinUninterruptibly()) != null) {
                for (ArrayList<KeyValue> row : rows) {
                    final byte[] key = row.get(0).key();
                    if (Bytes.memcmp(_metric, key, 0, METRIC_WIDTH) != 0) {
                        throw new IllegalArgumentException("HBase returned a row that doesn't match our scanner ("
                                + scanner + ")! " + row + " does not start with " + Arrays.toString(_metric));
                    }
                    Span span = spans.get(key);//每个指标、tags的kv 一个span
                    if (span == null) {
                        span = new Span(uniqueId);
                        spans.put(key, span);
                    }
                    final KeyValue compacted = compact(query, row);//多行聚合为一行---这里没有聚合函数的动作，只是把key单独提取出来
                    if (compacted != null) {
                        span.addRow(compacted, this.qualifierWidth);
                        nrows++;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询失败, findSpans fail, metric: {}, startTime: {}, endTime{}", query.getMetric(), query.getStartTime(), query.getEndTime(), e);
            throw new RuntimeException("Should never be here", e);
        } finally {
//            time.stop();
        }
        if (nrows == 0) {
            return null;
        }
        return spans;
    }


    private List<DataPoints> groupByAndAggregate(Query query, final Map<byte[], Span> spans, List<byte[]> tagKeyByteList,
                                                 long unixStartSeconds, long unixEndSeconds) {
//        Timer.Context time = Metrics.timer("KlTsdbGroupAndAggPointsTimer").get().time();
        try {
            if (spans == null || spans.isEmpty()) {
                return NO_RESULT;
            }

            final AggregatorType aggregator = query.getAggregator();
            final AggregatorType downSampler = query.getDownSampler();
            final int interval = query.getSampleInterval();

            if (tagKeyByteList.isEmpty()) {
                // We haven't been asked to find groups, so let's put all the spans
                // together in the same group.
                SpanGroup group = new SpanGroup(unixStartSeconds, unixEndSeconds, spans.values(), aggregator, downSampler, interval);
                List<DataPoints> points = Lists.newArrayListWithCapacity(1);
                points.add(group);
                return points;
            }

            // Maps group value IDs to the SpanGroup for those values. Say we've
            // been asked to group by two things: foo=* bar=* Then the keys in this
            // map will contain all the value IDs combinations we've seen. If the
            // name IDs for `foo' and `bar' are respectively [0, 0, 7] and [0, 0, 2]
            // then we'll have group_bys=[[0, 0, 2], [0, 0, 7]] (notice it's sorted
            // by ID, so bar is first) and say we find foo=LOL bar=OMG as well as
            // foo=LOL bar=WTF and that the IDs of the tag values are:
            // LOL=[0, 0, 1] OMG=[0, 0, 4] WTF=[0, 0, 3]
            // then the map will have two keys:
            // - one for the LOL-OMG combination: [0, 0, 1, 0, 0, 4] and,
            // - one for the LOL-WTF combination: [0, 0, 1, 0, 0, 3].
            final ByteMap<SpanGroup> tagValueSpanGroupMap = new ByteMap<SpanGroup>();
            final byte[] tagValuesBytes = new byte[tagKeyByteList.size() * TAG_WIDTH];
            for (final Entry<byte[], Span> entry : spans.entrySet()) {

                final byte[] rowKey = entry.getKey();
                byte[] tag_value_id = null;
                int i = 0;

                Map<byte[], byte[]> tag_byte_map = Tags.getTagsByteMapFromRowKey(rowKey);

                for (final byte[] tag_key_id : tagKeyByteList) {
                    tag_value_id = tag_byte_map.get(tag_key_id);
                    if (tag_value_id == null) {
                        break;
                    }
                    System.arraycopy(tag_value_id, 0, tagValuesBytes, i, TAG_VALUE_WIDTH);
                    i += TAG_VALUE_WIDTH;
                }

                // span不含分组所需tag，丢弃
                if (tag_value_id == null && !tagKeyByteList.isEmpty()) {
                    logger.error("WTF?  Dropping span for row " + Arrays.toString(rowKey)
                            + " as it had no matching tag from the requested groups," + " which is unexpected.  Query="
                            + this);
                    continue;
                }

                // 将span加入分组
                SpanGroup thegroup = tagValueSpanGroupMap.get(tagValuesBytes);
                if (thegroup == null) {
                    thegroup = new SpanGroup(unixStartSeconds, unixEndSeconds, null, aggregator, downSampler, interval);
                    /*
                     * Copy the array because we're going to keep `group' and
                     * overwrite its contents. So we want the collection to have an
                     * immutable copy.
                     */
                    final byte[] groupCopy = Arrays.copyOf(tagValuesBytes, tagValuesBytes.length);
                    tagValueSpanGroupMap.put(groupCopy, thegroup);
                }
                thegroup.add(entry.getValue());
            }
            return new ArrayList<DataPoints>(tagValueSpanGroupMap.values());
        } finally {
//            time.stop();
        }
    }

    /**
     * Helper comparison function to compare tag name IDs.
     *
     * @param name_width Number of bytes used by a tag name ID.
     * @param tag        A tag (array containing a tag name ID and a tag value ID).
     * @param group_by   A tag name ID.
     * @return {@code true} number if {@code tag} should be used next (because
     * it contains a smaller ID), {@code false} otherwise.
     */
    private boolean isTagNext(final short name_width, final byte[] tag, final byte[] group_by) {
        if (tag == null) {
            return false;
        } else if (group_by == null) {
            return true;
        }
        final int cmp = Bytes.memcmp(tag, group_by, 0, name_width);
        if (cmp == 0) {
            throw new AssertionError("invariant violation: tag ID " + Arrays.toString(group_by)
                    + " is both in 'tags' and 'group_bys'");
        }
        // merge compare
        return cmp < 0;
    }

    /**
     * Appends the given ID to the given buffer, followed by "\\E".
     */
    private void addId(final StringBuilder buf, final byte[] id) {
        boolean backslash = false;
        for (final byte b : id) {
            buf.append((char) (b & 0xFF));
            if (b == 'E' && backslash) {
                /*
                 * If we saw a `\' and now we have a `E'. So we just terminated
                 * the quoted section because we just added \E to `buf'. So
                 * let's put a litteral \E now and start quoting again.
                 */
                buf.append("\\\\E\\Q");
            } else {
                backslash = b == '\\';
            }
        }
        buf.append("\\E");
    }


    public KeyValue compact(Query query, List<KeyValue> cellList) {
        if (cellList == null || cellList.isEmpty()) {
            return null;
        }
        KeyValue kv = cellList.get(0);
        int value_count = RowKey.metricType(kv.key()).sequence().length;

        byte[] qualifiers = new byte[cellList.size() * qualifierWidth];
        byte[] values = new byte[cellList.size() * value_count * ONE_VALUE_WIDTH];

        int qualIndex = 0;
        int valueIndex = 0;

        for (KeyValue cell : cellList) {
            byte[] v = cell.value();
            byte[] qualifierBytes = cell.qualifier();
            short qualifier = Bytes.getShort(cell.qualifier());
            int remain = qualifier % 60;
            if (remain != 0) {/* qualifier取整 59 -> 0; 61 -> 60; 120 -> 120; 3560 -> 3540 */
                qualifierBytes = Bytes.fromShort((short) (qualifier - remain));
            }
            System.arraycopy(qualifierBytes, 0, qualifiers, qualIndex, qualifierWidth);
            System.arraycopy(v, 0, values, valueIndex, v.length);
            qualIndex += qualifierWidth;
            valueIndex += v.length;
        }
        return new KeyValue(kv.key(), kv.family(), qualifiers, values);
    }

    public Deferred<Object> flush() throws HBaseException {
        return client.flush();
    }

    public Deferred<Object> shutdown() {
        return client.shutdown();
    }

    /**
     * Comparator that ignores timestamps in row keys.
     */
    private final class SpanCmp implements Comparator<byte[]> {

        public int compare(final byte[] a, final byte[] b) {
            final int length = Math.min(a.length, b.length);
            if (a == b) {
                // Do this after accessing a.length and b.length in order to NPE
                // if either a or b is null.
                return 0;
            }

            // First compare the metric ID.
            int i;
            for (i = 0; i < METRIC_WIDTH; i++) {
                if (a[i] != b[i]) {
                    // "promote" to unsigned.
                    return (a[i] & 0xFF) - (b[i] & 0xFF);
                }
            }

            // Then skip the timestamp and compare the rest.
            for (i += TIMESTAMP_WIDTH; i < length; i++) {
                if (a[i] != b[i]) {
                    // "promote" to unsigned.
                    return (a[i] & 0xFF) - (b[i] & 0xFF);
                }
            }

            return a.length - b.length;
        }
    }

}
