package kelly.springboot.web.controller;


import kelly.springboot.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by kelly-lee on 2017/9/20.
 */

//    @RequestMapping 处理请求地址映射。
//    method – 指定请求的方法类型：POST/GET/DELETE/PUT 等
//    value – 指定实际的请求地址
//    consumes – 指定处理请求的提交内容类型，例如 Content-Type 头部设置application/json, text/html
//    produces – 指定返回的内容类型
//    @PathVariable URL 映射时，用于绑定请求参数到方法参数
//    @RequestBody 这里注解用于读取请求体 boy 的数据，通过 HttpMessageConverter 解析绑定到对象中
@RestController
@RequestMapping("/user")
//spring mvc controller 默认是单例，可通过@Scope配置单例或多例
@Scope(value = "singleton")
public class UserController {

    //TODO 更深入了解 HashMap,HashTable,Collections.synchronizedMap(),ConcurrentHashMap的实现却更加精细
    //Collections.synchronizedMap()可以接收任意Map实例，实现Map的同步
    //ConcurrentHashMap它必然是个HashMap
    //Collections.synchronizedMap()和Hashtable一样，实现上在调用map所有方法时，都对整个map进行同步
    //ConcurrentHashMap的实现却更加精细，它对map中的所有桶加了锁
    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<Long, User>());


   // @ApiOperation(value = "获取用户列表", notes = "")
    @RequestMapping(value = "/", method = RequestMethod.GET, consumes = "application/json")
    public List<User> showUserList() {
        System.out.println("aa-------------------------");
        return new ArrayList<User>(users.values());
    }


   // @ApiOperation(value = "创建用户", notes = "根据User对象创建用户")
  //  @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
    public String postUser(@ModelAttribute User user) {
        users.put(user.getId(), user);
        return "success";
    }

  //  @ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息")
  //  @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = "application/json")
    public User getUser(@PathVariable Long id) {
        return users.get(id);
    }

    //TODO 在postman中测试不通过
    //@ApiOperation注解来给API增加说明、通过@ApiImplicitParams、@ApiImplicitParam注解来给参数增加说明。
  //  @ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
  //  @ApiImplicitParams({
  //          @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long"),
  //          @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
  //  })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public String putUser(@PathVariable Long id, @ModelAttribute User user) {
        User _user = new User();
        _user.setId(id);
        _user.setName(user.getName());
        _user.setAge(user.getAge());
        users.put(id, _user);
        return "success";
    }

 //   @ApiOperation(value = "删除用户", notes = "根据url的id来指定删除对象")
 //   @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = "application/json")
    public String deleteUser(@PathVariable Long id) {
        users.remove(id);
        return "success";
    }


}
