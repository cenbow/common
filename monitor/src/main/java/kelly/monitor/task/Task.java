package kelly.monitor.task;

/**
 * Created by kelly-lee on 2017/9/27.
 */
public interface Task extends Runnable {

    public void setCron(String cron);

    public void start();

    public void stop();

}
