package kelly.zookeeper.observer;

import com.google.common.eventbus.Subscribe;

/**
 * Created by kelly.li on 17/11/18.
 */
public class EventResolver {


    @Subscribe
    public void handle(NodeAddedEvent event) {
        System.out.println("add " + event.getPath() + "," + event.getEndpoint() + "," + event.getLayer());
    }

    @Subscribe
    public void handle(NodeRemovedEvent event) {
        System.out.println("remove " + event.getPath() + "," + event.getEndpoint() + "," + event.getLayer());
    }


}
