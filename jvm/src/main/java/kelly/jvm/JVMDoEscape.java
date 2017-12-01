package kelly.jvm;

/**
 * Created by kelly-lee on 17/8/3.
 */
public class JVMDoEscape {

    /**
     * 在JVM运用的时候会通过反射的方式到Method区域找到入口类的入口方面
     * @param args
     */

    public static void main(String[] args) {
        //main方法放在Method方法
        Worker worker = new Worker();
        while(true){
            worker.useWorker();
        }
    }
}


class Worker{
    public Worker worker;

    public Worker getWorker(){
        //方法内申明对象被外部引用
        return null == worker?new Worker():worker;
    }

    public void setWorker(){
        worker = new Worker();
    }

    public void useWorker(){
        //内存逃逸
        Worker obj = getWorker();
    }

    public void useWorker2(){
        //
        Worker obj = new Worker();
    }
}
