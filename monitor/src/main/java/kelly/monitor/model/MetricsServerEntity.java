package kelly.monitor.model;

/**
 * Created by kelly-lee on 2017/10/12.
 */
public class MetricsServerEntity {

    private Integer tomcat_th;
    private Integer session_th;
    private String app_id;

    public MetricsServerEntity() {
    }

    public MetricsServerEntity(Integer tomcat_th, Integer session_th, String app_id) {
        this.tomcat_th = tomcat_th;
        this.session_th = session_th;
        this.app_id = app_id;
    }

    public Integer getTomcat_th() {
        return tomcat_th;
    }

    public void setTomcat_th(Integer tomcat_th) {
        this.tomcat_th = tomcat_th;
    }

    public Integer getSession_th() {
        return session_th;
    }

    public void setSession_th(Integer session_th) {
        this.session_th = session_th;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

}
