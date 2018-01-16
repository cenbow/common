package kelly.monitor.model;

import java.util.List;
import java.util.Map;

/**
 * Created by kelly-lee on 2017/10/12.
 */
public class MetricsDataEntity {

    private List<MetricsServerEntity> server;
    private Map<String, Integer> mysql;

    public List<MetricsServerEntity> getServer() {
        return server;
    }

    public void setServer(List<MetricsServerEntity> server) {
        this.server = server;
    }

    public Map<String, Integer> getMysql() {
        return mysql;
    }

    public void setMysql(Map<String, Integer> mysql) {
        this.mysql = mysql;
    }
}
