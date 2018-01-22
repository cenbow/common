package kelly.monitor.metric;

import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.List;

/**
 * Created by kelly-lee on 2018/1/22.
 */
public class Deltas {

    final static List<Delta> deltas = Lists.newCopyOnWriteArrayList();
    private static long lastUpdate = 0;
    private static Calendar calendar = Calendar.getInstance();


    void add(Delta delta) {
        deltas.add(delta);
    }

    void tick() {
        long current = System.currentTimeMillis();
        if (current - lastUpdate < 50000L) {
            return;
        }
        calendar.setTimeInMillis(current);
        if (calendar.get(Calendar.SECOND) > 10) {
            return;
        }
        lastUpdate = current;
        for (Delta delta : deltas) {
            delta.tick();
        }
    }
}
