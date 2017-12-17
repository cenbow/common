package kelly.rocketmq.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JacksonSerializer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    static {

        //是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //是否将允许使用非双引号属性名字
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否允许单引号来包住属性名称和字符串值
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //允许接受所有引号引起来的字符，使用‘反斜杠\’机制：如果不允许，只有JSON标准说明书中 列出来的字符可以被避开约束。
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

        //允许parser可以识别"Not-a-Number" (NaN)标识集合作为一个合法的浮点数
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        //允许JSON整数以多个0开始
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);

        //当调用 intern 方法时，如果池已经包含一个等于此 String 对象的字符串（该对象由 equals(Object) 方法确定），则返回池中的字符串。
        //否则，将此 String 对象添加到池中， 并且返回此 String 对象的引用
        //如果允许，则JSON所有的属性名将会 intern() , 必须设置CANONICALIZE_FIELD_NAMES为true
        //mapper.configure(JsonParser.Feature.INTERN_FIELD_NAMES, true);
        //属性名称是否被规范化
        //mapper.configure(JsonParser.Feature.CANONICALIZE_FIELD_NAMES, true);
        //最基础的生成器，使用默认pretty printer
        //mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

        //是否缩放排列输出
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        //json串中有属性，序列化对象中没有则忽略
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    public static ObjectMapper getMapper() {
        return mapper;
    }

    public String serialize(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            logger.error("serialize error: ", e);
            return null;
        }
    }


    public <T> T deSerialize(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (IOException e) {
            logger.error("deserialize error: {} -> {}", content, clazz, e);
            return null;
        }
    }
}