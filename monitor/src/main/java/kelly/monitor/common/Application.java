package kelly.monitor.common;

import com.google.common.collect.Sets;
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
    private Set<String> owners = Sets.newHashSet("kelly.li");
    private Set<String> emails = Sets.newHashSet("kelly.li@gmail.com");
    private Set<ApplicationServer> applicationServers;

    public enum Status {
        ENABLE, DISABLE;
    }
}
