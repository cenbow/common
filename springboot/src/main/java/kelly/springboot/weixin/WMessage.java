package kelly.springboot.weixin;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kelly.li on 17/10/22.
 */
public class WMessage {

    public enum keys {
        ToUserName,
        FromUserName,
        CreateTime,
        MsgType,
        MsgId,
        Content,
        MediaId
    }


    HashMap<String, Object> attrs = new HashMap<>();

    public Map<String, Object> getAttrs() {
        return Collections.unmodifiableMap(attrs);
    }

    private static final Set<String> keyNames = Sets.newHashSet();

    static {
        for (keys key : keys.values())
            keyNames.add(key.name());
    }
}
