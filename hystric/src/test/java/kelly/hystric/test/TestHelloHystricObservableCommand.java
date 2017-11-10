package kelly.hystric.test;

import kelly.hystric.command.HelloHystrixCommand;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.io.IOException;

/**
 * Created by kelly-lee on 2017/11/8.
 */
public class TestHelloHystricObservableCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHelloHystricObservableCommand.class);

    @Test
//17:40:16.420 [main] INFO consumer.TestHystricObservableCommand - ObservableHelloCommand run
//17:40:16.425 [main] INFO consumer.TestHystricObservableCommand - before blocking
//17:40:16.436 [main] INFO consumer.TestHystricObservableCommand - result Hi kelly!
//17:40:16.437 [main] INFO consumer.TestHystricObservableCommand - after blocking

    public void testObserveBlocking() {
        // observe()是异步非堵塞性执行，同queue
        Observable<String> hotObservable = new HelloHystrixCommand("kelly").observe();
        // single()是堵塞的
        LOGGER.info("before blocking");
        LOGGER.info("result {}", hotObservable.toBlocking().single());
        LOGGER.info("after blocking");
    }

    @Test
//17:44:00.171 [main] INFO consumer.TestHystricObservableCommand - ObservableHelloCommand construct
//17:44:00.175 [main] INFO consumer.TestHystricObservableCommand - before subscribe
//17:44:00.176 [main] INFO consumer.TestHystricObservableCommand - after subscribe
//17:44:00.177 [RxIoScheduler-2] INFO consumer.TestHystricObservableCommand - onNext Hi kelly!
//17:44:00.178 [RxIoScheduler-2] INFO consumer.TestHystricObservableCommand - onCompleted

    public void testObserveSubscribe() throws IOException {
        // observe()是异步非堵塞性执行，同queue
        Observable<String> hotObservable = new HelloHystrixCommand("kelly").observe();
        // 注册观察者事件
        // subscribe()是非堵塞的
        LOGGER.info("before subscribe");
        hotObservable.subscribe(new Observer<String>() {

            // 先执行onNext再执行onCompleted
            // @Override
            public void onCompleted() {
                LOGGER.info("onCompleted");
            }

            // @Override
            public void onError(Throwable e) {
                LOGGER.error("onError", e);
            }

            // @Override
            public void onNext(String v) {
                LOGGER.info("onNext {}", v);
            }
        });
        LOGGER.info("after subscribe");
        System.in.read();
    }

    @Test
//success
//17:46:39.955 [main] INFO consumer.TestHystricObservableCommand - ObservableHelloCommand construct
//17:46:39.967 [main] INFO consumer.TestHystricObservableCommand - before subscribe
//17:46:39.988 [main] INFO consumer.TestHystricObservableCommand - call Hi kelly!
//17:46:39.989 [main] INFO consumer.TestHystricObservableCommand - after subscribe

    public void testObserveSubscribeAction() throws IOException {
        Observable<String> hotObservable = new HelloHystrixCommand("kelly").observe();
        LOGGER.info("before subscribe");
        // - ignore errors and onCompleted signal
        hotObservable.subscribe(new Action1<String>() {

            // 相当于上面的onNext()
            // @Override
            public void call(String v) {
                LOGGER.info("call {}", v);
            }

        });
        LOGGER.info("after subscribe");
        System.in.read();
    }

    @Test
//17:48:20.812 [main] INFO consumer.TestHystricObservableCommand - before blocking
//17:48:20.841 [main] INFO consumer.TestHystricObservableCommand - ObservableHelloCommand construct
//17:48:20.849 [main] INFO consumer.TestHystricObservableCommand - result Hi kelly!
//17:48:20.849 [main] INFO consumer.TestHystricObservableCommand - after blocking

    public void testToObservableBlocking() {
        // toObservable()是异步非堵塞性执行，同queue
        Observable<String> coldObservable = new HelloHystrixCommand("kelly").toObservable();
        // single()是堵塞的
        LOGGER.info("before blocking");
        LOGGER.info("result {}", coldObservable.toBlocking().single());
        LOGGER.info("after blocking");
    }

    @Test
//17:50:04.356 [main] INFO consumer.TestHystricObservableCommand - before subscribe
//17:50:04.447 [main] INFO consumer.TestHystricObservableCommand - ObservableHelloCommand construct
//17:50:04.454 [main] INFO consumer.TestHystricObservableCommand - after subscribe
//17:50:04.457 [RxIoScheduler-2] INFO consumer.TestHystricObservableCommand - onNext Hi kelly!
//17:50:04.459 [RxIoScheduler-2] INFO consumer.TestHystricObservableCommand - onCompleted

    public void testToObservableSubscribe() throws IOException {
        // toObservable()是异步非堵塞性执行，同queue
        Observable<String> coldObservable = new HelloHystrixCommand("kelly").toObservable();
        LOGGER.info("before subscribe");
        coldObservable.subscribe(new Observer<String>() {

            // 先执行onNext再执行onCompleted
            // @Override
            public void onCompleted() {
                LOGGER.info("onCompleted");
            }

            // @Override
            public void onError(Throwable e) {
                LOGGER.error("onError", e);
            }

            // @Override
            public void onNext(String v) {
                LOGGER.info("onNext {}", v);
            }
        });
        LOGGER.info("after subscribe");
        System.in.read();
    }


    @Test
//17:52:02.155 [main] INFO consumer.TestHystricObservableCommand - before subscribe
//17:52:02.183 [main] INFO consumer.TestHystricObservableCommand - ObservableHelloCommand construct
//17:52:02.187 [main] INFO consumer.TestHystricObservableCommand - after subscribe
//17:52:02.192 [RxIoScheduler-2] INFO consumer.TestHystricObservableCommand - call Hi kelly!

    public void testToObservableSubscribeAction() throws IOException {
        Observable<String> coldObservable = new HelloHystrixCommand("kelly").toObservable();
        LOGGER.info("before subscribe");
        // - ignore errors and onCompleted signal
        coldObservable.subscribe(new Action1<String>() {
            // 相当于上面的onNext()
            // @Override
            public void call(String v) {
                LOGGER.info("call {}", v);
            }

        });
        LOGGER.info("after subscribe");
        System.in.read();
    }


}
