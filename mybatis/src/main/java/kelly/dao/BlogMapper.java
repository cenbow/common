package kelly.dao;

import kelly.model.Blog;
import org.apache.ibatis.annotations.Select;

/**
 * Created by kelly-lee on 17/9/10.
 */
public interface BlogMapper {
    @Select("SELECT * FROM blog WHERE id = #{id}")
    Blog selectBlog(int id);
}
