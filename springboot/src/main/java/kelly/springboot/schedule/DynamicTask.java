package kelly.springboot.schedule;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by kelly-lee on 2017/9/27.
 */
@Component
public class DynamicTask extends BaseTask{

    public DynamicTask() {
        setCron("0/10 * * * * *");
    }

    @Override
    public void run() {
        System.out.println("task run on " + new Date());
    }


}
