package kelly.springboot.web.websocket;

import com.google.common.eventbus.Subscribe;
import kelly.springboot.config.CustomSpringConfigurator;
import kelly.springboot.config.JacksonSerializer;
import kelly.springboot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by kelly-lee on 2017/10/16.
 */

@ServerEndpoint(value = "/ws", configurator = CustomSpringConfigurator.class)
@Component
@Scope("prototype")
public class UserWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(UserWebSocket.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JacksonSerializer jacksonSerializer;

    private Session session;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        logger.info("onOpen");
        this.session = session;
    }

    @OnClose
    public void onClose() throws IOException {
        logger.info("onClose");
    }

    @OnMessage
    public void onMessage(String text) {
        logger.info("onMessage");
        this.session.getAsyncRemote().sendText(text);
    }


    @Subscribe
    public void sendMessage(String text) throws IOException {
        logger.info("sendMessage");
        this.session.getAsyncRemote().sendText(text);
    }

}
