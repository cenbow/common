package kelly.monitor.model;

/**
 * Created by kelly-lee on 2017/10/12.
 */
public class Metrics {

    private Long id;
    private String name;
    private Float value;
    private String appId;
    private Long createdTime;

    public Metrics() {
    }

    public Metrics(String name, float value, String appId, Long createdTime) {
        this.name = name;
        this.value = value;
        this.appId = appId;
        this.createdTime = createdTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }
}
