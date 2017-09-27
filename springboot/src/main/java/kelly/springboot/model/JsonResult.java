package kelly.springboot.model;

/**
 * Created by kelly-lee on 2017/9/21.
 */
public class JsonResult<T> {

    private String code;
    private String message;
    private String url;
    private T data;

    public static final String CODE_OK = "0";
    public static final String CODE_ERROR = "1";

    public static final String MESSAGE_OK = "ok";

    public JsonResult(T data) {
        this.code = CODE_OK;
        this.message = MESSAGE_OK;
        this.data = data;
    }

    public JsonResult(String code, String message, String url) {
        this.code = code;
        this.message = message;
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
