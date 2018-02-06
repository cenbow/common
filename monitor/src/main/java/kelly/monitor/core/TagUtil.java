package kelly.monitor.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import kelly.monitor.common.ApplicationServer;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagUtil {

    static final Splitter SPLIT_COLON = Splitter.on(';').trimResults();

    public static final String TAG_NAME_APP = "app";
    public static final String TAG_NAME_HOST = "host";
    public static final String TAG_VALUE_ALL = "*";
    public static final String TAG_VALUE_TOP = "#TOP";
    public static final String TAG_VALUE_BOTTOM = "#BOTTOM";
    public static final String TAG_VALUE_DELTA = "#DELTA";

    public static final List<String> excludeTagKeyList = Lists.newArrayList(TAG_VALUE_ALL, TAG_VALUE_TOP,
            TAG_VALUE_BOTTOM, TAG_VALUE_DELTA);

    public static final char TAG_VALUE_SPLIT = '|';

    public static Map<String, String> parse(String tags) {
        if (StringUtils.isEmpty(tags)) {
            return new HashMap<String, String>();
        }
        Map<String, String> map = new HashMap<String, String>();
        for (String tag : SPLIT_COLON.splitToList(tags)) {
            int idx = tag.indexOf(':');
            if (idx > 0 && idx + 1 < tag.length()) {
                map.put(tag.substring(0, idx), tag.substring(idx + 1));
            }
        }
        return map;
    }

    public static Map<String, String> fixDefaultTag(ApplicationServer server, Map<String, String> tags) {
        if (tags == null) {
            tags = new HashMap<String, String>(2);
        }
        tags.put(TAG_NAME_APP, server.getAppCode());
        tags.put(TAG_NAME_HOST, server.getIp());
        return tags;
    }

    public static void main(String[] args) {
        System.out.println(TagUtil.parse("app:monitor;host:127.0.0.1"));
    }
}
