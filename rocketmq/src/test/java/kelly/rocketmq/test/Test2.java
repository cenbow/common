package kelly.rocketmq.test;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import kelly.rocketmq.client.MessageProducer;
import kelly.rocketmq.entity.Order;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by kelly.li on 17/12/17.
 */
public class Test2 {

    MessageProducer messageProducer = new MessageProducer();

    @Before
    public void before() {
        try {
            messageProducer.init();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test1() throws MQClientException {
        Order order = new Order(UUID.randomUUID().toString(), "kelly", new BigDecimal(5000));
        Message message = messageProducer.generateMessage("consume", order.getOrderId(), order);
        SendResult sendResult = messageProducer.sendSyncMessage(message);
        System.out.println(sendResult);
    }

    @Test
    public void test2() throws MQClientException {
        Order order = new Order(UUID.randomUUID().toString(), "kelly", new BigDecimal(5000));
        Message message = messageProducer.generateMessage("consume", order.getOrderId(), order);
        messageProducer.sendAsyncMessage(message);
    }


}
