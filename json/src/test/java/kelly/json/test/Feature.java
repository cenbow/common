//package kelly.json.test;
//
//import org.codehaus.jackson.map.MapperConfig;
//
//public enum SeriFeature implements MapperConfig.ConfigFeature {
//        /*
//        /******************************************************
//        /*  Introspection features
//        /******************************************************
//         */
//
//    /**
//     * 注解扫描配置
//     */
//    USE_ANNOTATIONS(true),
//
//    /**
//     * 获取getter方法，前缀为get
//     */
//    AUTO_DETECT_GETTERS(true),
//
//    /**
//     * 获取getter方法，前缀为is
//     */
//    AUTO_DETECT_IS_GETTERS(true),
//
//    /**
//     * 将对象所有的field作为json属性
//     */
//    AUTO_DETECT_FIELDS(true),
//
//    /**
//     * 该特性决定当访问属性时候，方法和field访问是否修改设置。 如果设置为true，
//     * 则通过反射调用方法AccessibleObject#setAccessible 来允许访问不能访问的对象。
//     */
//    CAN_OVERRIDE_ACCESS_MODIFIERS(true),
//
//    /**
//     * 获取的getter方法需要setter方法，否则，所有发现的getter都可以作为getter方法。
//     */
//    REQUIRE_SETTERS_FOR_GETTERS(false),
//
//        /*
//        /******************************************************
//        /* Generic output features
//        /******************************************************
//         */
//
//    /**
//     * 属性对应的值为null，是否需要写出来，write out。
//     */
//    @Deprecated
//    WRITE_NULL_PROPERTIES(true),
//
//    /**
//     * 特征决定是使用运行时动态类型，还是声明的静态类型。
//     * 也可以使用{@link JsonSerialize#typing} 注解属性
//     */
//    USE_STATIC_TYPING(false),
//
//    /**
//     * 该特性决定拥有view注解{@link org.codehaus.jackson.map.annotate.JsonView}的属性是否在JSON序列化视图中。如果true，则非注解视图，也包含；
//     * 否则，它们将会被排除在外。
//     */
//    DEFAULT_VIEW_INCLUSION(true),
//
//    /**
//     * 在JAVA中配置XML root{@XmlRootElement.name}注解,最后xml数据中会出现对应root根name.
//     */
//    WRAP_ROOT_VALUE(false),
//
//    /**
//     * 该特性对于最基础的生成器，使用默认pretty printer {@link org.codehaus.jackson.JsonGenerator#useDefaultPrettyPrinter}
//     * 这只会对{@link org.codehaus.jackson.JsonGenerator}有影响.该属性值允许使用默认的实现。
//     */
//    INDENT_OUTPUT(false),
//
//    /**
//     * 是否对属性使用排序，默认排序按照字母顺序。
//     */
//    SORT_PROPERTIES_ALPHABETICALLY(false),
//
//        /*
//        /******************************************************
//        /*  Error handling features
//        /******************************************************
//         */
//
//    /**
//     * 是否允许一个类型没有注解表明打算被序列化。默认true，抛出一个异常；否则序列化一个空对象，比如没有任何属性。
//     * <p>
//     * Note that empty types that this feature has only effect on
//     * those "empty" beans that do not have any recognized annotations
//     * (like <code>@JsonSerialize</code>): ones that do have annotations
//     * do not result in an exception being thrown.
//     *
//     * @since 1.4
//     */
//    FAIL_ON_EMPTY_BEANS(true),
//
//    /**
//     * 封装所有异常
//     */
//    WRAP_EXCEPTIONS(true),
//
//        /*
//        /******************************************************
//        /* Output life cycle features
//        /******************************************************
//         */
//
//    /**
//     * 该特性决定序列化root级对象的实现closeable接口的close方法是否在序列化后被调用。
//     * <p>
//     * 注意：如果true，则完成序列化后就关闭；如果，你可以在处理最后，调用排序操作等，则为false。
//     */
//    CLOSE_CLOSEABLE(false),
//
//    /**
//     * 该特性决定是否在writeValue()方法之后就调用JsonGenerator.flush()方法。
//     * 当我们需要先压缩，然后再flush，则可能需要false。
//     */
//    FLUSH_AFTER_WRITE_VALUE(true),
//
//        /*
//        /******************************************************
//        /* Data type - specific serialization configuration
//        /******************************************************
//         */
//
//    /**
//     * 该特性决定是否将基于Date的值序列化为timestamp数字式的值，或者作为文本表示。
//     * 如果文本表示，则实际格式化的时候会调用{@link #getDateFormat}方法。
//     * <p>
//     * 该特性可能会影响其他date相关类型的处理，虽然我们理想情况是只对date起作用。
//     */
//    WRITE_DATES_AS_TIMESTAMPS(true),
//
//    /**
//     * 是否将Map中得key为Date的值，也序列化为timestamps形式（否则，会被序列化为文本形式的值）。
//     */
//    WRITE_DATE_KEYS_AS_TIMESTAMPS(false),
//
//    /**
//     * 该特性决定怎样处理类型char[]序列化，是否序列化为一个显式的JSON数组，还是默认作为一个字符串。
//     */
//    WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS(false),
//
//    /**
//     * 该特性决定对Enum 枚举值使用标准的序列化机制。如果true，则返回Enum.toString()值，否则为Enum.name()
//     */
//    WRITE_ENUMS_USING_TO_STRING(false),
//
//    /**
//     * 这个特性决定Java枚举值是否序列化为数字（true）或者文本值（false）.如果是值的话，则使用Enum.ordinal().
//     * 该特性优先级高于上面的那个。
//     *
//     * @since 1.9
//     */
//    WRITE_ENUMS_USING_INDEX(false),
//
//    /**
//     * 决定是否Map的带有null值的entry被序列化（true）
//     */
//    WRITE_NULL_MAP_VALUES(true),
//
//    /**
//     * 决定容器空的属性（声明为Collection或者array的值）是否被序列化为空的JSON数组（true），否则强制输出。
//     * <p>
//     * Note that this does not change behavior of {@link java.util.Map}s, or
//     * "Collection-like" types.
//     *
//     * @since 1.9
//     */
//    WRITE_EMPTY_JSON_ARRAYS(true);
//
//    final boolean _defaultState;
//
//    private Feature(boolean defaultState) {
//        _defaultState = defaultState;
//    }
//
//
//    public boolean enabledByDefault() {
//        return _defaultState;
//    }
//
//
//    public int getMask() {
//        return (1 << ordinal());
//    }
//}