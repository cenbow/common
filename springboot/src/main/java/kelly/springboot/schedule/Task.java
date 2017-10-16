package kelly.springboot.schedule;

/**
 * Created by kelly-lee on 2017/9/27.
 */
public interface Task extends Runnable {
    public void setCron(String cron);
}
