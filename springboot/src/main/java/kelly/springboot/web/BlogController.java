package kelly.springboot.web;

import kelly.springboot.dao.BlogMapper;
import kelly.springboot.model.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kelly.li on 17/9/16.
 */
@RestController
public class BlogController {


    @Autowired
    private BlogMapper blogMapper;

    @RequestMapping("/blog")
    public Blog getBlog(Integer id) {
        Blog blog = blogMapper.selectBlog(id);
        System.out.println(blog);
        return blog;
    }
}
