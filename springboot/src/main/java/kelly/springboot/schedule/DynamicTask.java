package kelly.springboot.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by kelly-lee on 2017/9/27.
 */
@Component
public class DynamicTask extends BaseTask{

    private Logger logger = LoggerFactory.getLogger(DynamicTask.class);

    public DynamicTask() {
        setCron("0/10 * * * * *");
    }

    @Override
    public void run() {
        logger.info("task run on {}", new Date());
    }


}
