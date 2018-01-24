package kelly.monitor.model;

/**
 * Created by kelly-lee on 2018/1/17.
 */
public class Point {

    private long x;
    private float y;

    public Point(long x, float y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
