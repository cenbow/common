package kelly.jvm;

/**
 * Created by kelly.li on 17/8/2.
 */
//-verbose:gc -Xms10M -Xmx10M -XX:MaxDirectMemorySize=5M -Xss160k -XX:+PrintGCDetails
public class HelloStackOverFlow {

    private int count;

    public void count() {
        count++;
        count();
    }

    public static void main(String[] args) {
        System.out.println("HelloStackOverFlow");
        new HelloStackOverFlow().count();
    }


}
