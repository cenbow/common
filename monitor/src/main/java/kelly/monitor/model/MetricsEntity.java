package kelly.monitor.model;

/**
 * Created by kelly-lee on 2017/10/12.
 */
public class MetricsEntity {

    private String message;
    private Integer status;
    private MetricsDataEntity data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MetricsDataEntity getData() {
        return data;
    }

    public void setData(MetricsDataEntity data) {
        this.data = data;
    }
}
