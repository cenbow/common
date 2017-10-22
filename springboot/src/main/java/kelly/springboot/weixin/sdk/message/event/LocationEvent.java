package kelly.springboot.weixin.sdk.message.event;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by kelly.li on 17/10/22.
 */

public class LocationEvent extends Event {

    @JacksonXmlProperty(localName = "Latitude")
    private Double latitude;
    @JacksonXmlProperty(localName = "Longitude")
    private Double longitude;
    @JacksonXmlProperty(localName = "Precision")
    private Double precision;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }
}
