package kelly.monitor.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by kelly-lee on 2018/1/31.
 */
public class User {
    private String name;
    private String pwd;
    @DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private Date birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
