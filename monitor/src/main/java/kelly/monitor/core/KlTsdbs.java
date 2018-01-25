package kelly.monitor.core;

import com.google.common.base.Splitter;
import com.stumbleupon.async.Deferred;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.core.kltsdb.DefaultKlTsdb;
import kelly.monitor.core.uid.DatabaseUniqueId;
import kelly.monitor.core.uid.UniqueId;
import kelly.monitor.model.MetricsChart;
import org.hbase.async.HBaseClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Created by kelly-lee on 2018/1/25.
 */
public class KlTsdbs {

    private DefaultKlTsdb klTsdb;

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    String url = "jdbc:mysql://127.0.0.1:3306/kltsdb?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&&useOldAliasMetadataBehavior=true";
    String username = "root";
    String password = "root";
    String driverClassName = "com.mysql.jdbc.Driver";
    String table = "kltsdb";
    String quorum_spec = "47.95.230.71";

    public KlTsdbs() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        dataSource.setDriverClassName(driverClassName);
        UniqueId uniqueId = new DatabaseUniqueId(dataSource);
        HBaseClient client = new HBaseClient(quorum_spec);
        klTsdb = new DefaultKlTsdb(client, table, uniqueId);
    }

    public Deferred<Object> addPointAsync(IncomingPoint dataPoint) {
        return klTsdb.addPoints(dataPoint.getName(), dataPoint.getType(), dataPoint.getTimestamp(), dataPoint.getValues(), dataPoint.getTags());
    }

    public Object addPoint(IncomingPoint dataPoint) {
        try {
            return addPointAsync(dataPoint).join();
        } catch (Exception e) {
            return e;
        }
    }

    public MetricsChart initMetricsChart() throws Exception {
        return null;
    }


}
