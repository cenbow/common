package kelly.springboot.web.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Created by a1800101471 on 2017/9/27.
 */
@Controller
public class VelocityController {

    @RequestMapping("/v")
    public String index(Map<String, Object> model){
        Map<String, String> mapData = Maps.newHashMap();
        for (int i = 0; i < 10; i++) {
            //  mapData.put("key" + i, "value" + i);
        }
        model.put("map", mapData);

        List<String> listData = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            // listData.add("element" + i);
        }
        model.put("list", listData);
        return "v";
    }
}
