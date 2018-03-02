Map<String, AlertTagConfig> map = jacksonSerializer.mapper.readValue(json, new TypeReference<Map<String, AlertTagConfig>>() {
反序列化集合类型时，bean中有多个构造方法，方法参数特殊类型要写在前面,只能有2个构造方法