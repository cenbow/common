package kelly.monitor.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import kelly.monitor.config.JacksonSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * Created by kelly-lee on 2018/2/6.
 */
@Setter
@Getter
@ToString
public class Application {
    private Integer id;
    private String appName;
    private String appCode;
    private String description;
    private Status status;
    private Set<String> ownerCodes = Sets.newHashSet();
    private Set<Owner> owners = Sets.newHashSet();
    private Set<ApplicationServer> applicationServers;
    private String ownerJson;


    public enum Status {
        ENABLE, DISABLE;
    }

    public void load(JacksonSerializer jacksonSerializer) {
        if (!Strings.isNullOrEmpty(ownerJson)) {
            ownerCodes = jacksonSerializer.deSerialize(ownerJson, new TypeReference<Set<String>>() {
            });
        }
    }
}
