package kelly.monitor.model;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by kelly-lee on 2017/10/13.
 */
public class MetricsChart {

    private String name;
    private Set<String> date = new LinkedHashSet<String>();
    private Map<String, List<Float>> data = new HashMap<String, List<Float>>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");


    public MetricsChart() {

    }

    public MetricsChart(String name) {
        this.name = name;
    }

//    public void add(Metrics metrics) {
//        date.add(simpleDateFormat.format(new Date(metrics.getCreatedTime())));
//        add(metrics.getAppId(), metrics.getValue());
//    }

    public void add(String appId, Float value) {
        if (!data.containsKey(appId)) {
            List<Float> values = new ArrayList<Float>();
            values.add(value);
            data.put(appId, values);
        } else {
            data.get(appId).add(value);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<String> getDate() {
        return date;
    }

    public Map<String, List<Float>> getData() {
        return data;
    }

    public void setDate(Set<String> date) {
        this.date = date;
    }

    public void setData(Map<String, List<Float>> data) {
        this.data = data;
    }
}
