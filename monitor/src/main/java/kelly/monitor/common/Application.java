package kelly.monitor.common;

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
    private String appName;
    private String appCode;
    private Set<String> owners;
    private Set<String> emails;
    private Set<ApplicationServer> applicationServers;
}
