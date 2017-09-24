package kelly.model;

import java.io.Serializable;

/**
 * Created by kelly.li on 17/9/10.
 */
public class Blog implements Serializable {
    private Integer id;

    public Blog() {
    }

    public Blog(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                '}';
    }
}
