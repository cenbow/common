package kelly.rocketmq.client;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import kelly.rocketmq.config.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;

/**
 * Created by kelly.li on 17/12/17.
 */
public class MessageProducer {


    private String group = "default";
    private String topic = "TopicTest";
    private String namesrvAddr = "localhost:9876";
    private JacksonSerializer jacksonSerializer = new JacksonSerializer();
    private SendCallback defaultSendCallback = new DefaultSendCallback();

    private DefaultMQProducer producer;

    private Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();
    }

    public SendResult sendSyncMessage(Message message) {
        try {
            return producer.send(message);
        } catch (Exception e) {
            LOGGER.error("send message fail ", e);
            return null;
        }
    }

    public void sendAsyncMessage(Message message) {
        try {
            producer.send(message, defaultSendCallback);
        } catch (Exception e) {
            LOGGER.error("send message fail ", e);
        }
    }

    public void sendOnewayMessage(Message message) {
        try {
            producer.sendOneway(message);
        } catch (Exception e) {
            LOGGER.error("send message fail ", e);
        }
    }

    public Message generateMessage(String tag, String keys, Object data) {
        String jsonData = jacksonSerializer.serialize(data);
        try {
            return new Message(topic, tag, keys,
                    jsonData.getBytes(RemotingHelper.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("generate message fail ", e);
            return null;
        }
    }

    class DefaultSendCallback implements SendCallback {

        @Override
        public void onSuccess(SendResult sendResult) {
            LOGGER.info("DefaultSendCallback onSuccess {}", sendResult);
        }

        @Override
        public void onException(Throwable throwable) {
            LOGGER.info("DefaultSendCallback onException", throwable);
        }
    }

    @PreDestroy
    public void destory() {
        producer.shutdown();
    }
}
