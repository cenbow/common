package kelly.hystric.test;

import kelly.hystric.command.HelloHystrixCommand;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by kelly-lee on 2017/11/8.
 */
public class TestHelloHystrixCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHelloHystrixCommand.class);


    @Test
    public void testExecute() {
        String result = new HelloHystrixCommand("kelly").execute();
        LOGGER.info("result {}", result);
    }

    @Test
    public void testQueue() throws ExecutionException, InterruptedException {
        Future<String> future = new HelloHystrixCommand("kelly").queue();
        LOGGER.info("result {}", future.get());
    }

    @Test
//成功
//16:59:54.335 [main] INFO consumer.TestObservable - before blocking
//16:59:54.362 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloHystrixCommand run
//16:59:54.875 [main] INFO consumer.TestObservable - result Hello kelly
//16:59:54.875 [main] INFO consumer.TestObservable - after blocking
//失败配置fallback
//14:39:11.409 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - before blocking
//14:39:11.439 [hystrix-HelloHystrixGroup-1] INFO kelly.hystric.command.HelloHystrixCommand - HelloHystrixCommand run
//14:39:11.967 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - result Hello fallback kelly
//14:39:11.967 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - after blocking

    public void testObserveBlocking() {
        // observe()是异步非堵塞性执行，同queue
        Observable<String> hotObservable = new HelloHystrixCommand("kelly").observe();
        // single()是堵塞的
        LOGGER.info("before blocking");
        LOGGER.info("result {}", hotObservable.toBlocking().single());
        LOGGER.info("after blocking");
    }

    @Test

//成功
//16:57:45.205 [main] INFO consumer.TestObservable - before subscribe
//16:57:45.206 [main] INFO consumer.TestObservable - after subscribe
//16:57:45.236 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloHystrixCommand run
//16:57:45.748 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - onNext Hello kelly
//16:57:45.751 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - onCompleted
//失败配置fallback
//14:48:11.749 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - before subscribe
//14:48:11.750 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - after subscribe
//14:48:11.763 [hystrix-HelloHystrixGroup-1] INFO kelly.hystric.command.HelloHystrixCommand - HelloHystrixCommand run
//15:10:25.964 [hystrix-HelloHystrixGroup-1] INFO kelly.hystric.test.TestHelloHystrixCommand - onNext Hello fallback kelly
//15:10:25.967 [hystrix-HelloHystrixGroup-1] INFO kelly.hystric.test.TestHelloHystrixCommand - onCompleted
//失败没有配置fallback
//17:16:47.038 [main] INFO consumer.TestObservable - before subscribe
//17:16:47.039 [main] INFO consumer.TestObservable - after subscribe
//17:16:47.053 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloHystrixCommand run
//17:16:47.578 [hystrix-HelloHystrixGroup-1] ERROR consumer.TestObservable - onError
    public void testObserveSubscribe() throws IOException {
        // observe()是异步非堵塞性执行，同queue
        Observable<String> hotObservable = new HelloHystrixCommand("kelly").observe();
        // 注册观察者事件
        // subscribe()是非堵塞的
        LOGGER.info("before subscribe");
        hotObservable.subscribe(new Observer<String>() {

            // 先执行onNext再执行onCompleted
            // 成功或失败配置callfack时调用
            // @Override
            public void onCompleted() {
                LOGGER.info("onCompleted");
            }

            // @Override
            //失败没有配置callback时调用
            public void onError(Throwable e) {
                LOGGER.error("onError", e);
            }

            // @Override
            // 成功或失败配置callfack时调用
            public void onNext(String v) {
                LOGGER.info("onNext {}", v);
            }
        });
        LOGGER.info("after subscribe");
        System.in.read();
    }

    @Test
//成功
//17:04:20.585 [main] INFO consumer.TestObservable - before subscribe
//17:04:20.593 [main] INFO consumer.TestObservable - after subscribe
//17:04:20.601 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloCommand run
//17:04:21.109 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - call Hello kell
//失败配置fallback
//15:25:48.515 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - before subscribe
//15:25:48.520 [main] INFO kelly.hystric.test.TestHelloHystrixCommand - after subscribe
//15:25:48.527 [hystrix-HelloHystrixGroup-1] INFO kelly.hystric.command.HelloHystrixCommand - HelloHystrixCommand run
//15:25:49.065 [hystrix-HelloHystrixGroup-1] INFO kelly.hystric.test.TestHelloHystrixCommand - call Hello fallback kelly
//失败没有配置fallback
//17:06:54.566 [main] INFO consumer.TestObservable - before subscribe
//17:06:54.581 [main] INFO consumer.TestObservable - after subscribe
//17:06:54.582 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloCommand run
    public void testObserveSubscribeAction() throws IOException {
        Observable<String> hotObservable = new HelloHystrixCommand("kelly").observe();
        LOGGER.info("before subscribe");
        // - ignore errors and onCompleted signal
        hotObservable.subscribe(new Action1<String>() {

            // 相当于上面的onNext()
            // 成功或失败配置callfack时调用
            // @Override
            public void call(String v) {
                LOGGER.info("call {}", v);
            }

        });
        LOGGER.info("after subscribe");
        System.in.read();
    }

    @Test
//17:24:18.579 [main] INFO consumer.TestObservable - before blocking
//17:24:18.612 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloCommand run
//17:24:19.122 [main] INFO consumer.TestObservable - result Hello kelly
//17:24:19.123 [main] INFO consumer.TestObservable - after blocking

    public void testToObservableBlocking() {
        // toObservable()是异步非堵塞性执行，同queue
        Observable<String> coldObservable = new HelloHystrixCommand("kelly").toObservable();
        // single()是堵塞的
        LOGGER.info("before blocking");
        LOGGER.info("result {}", coldObservable.toBlocking().single());
        LOGGER.info("after blocking");
    }

    @Test
//17:27:12.991 [main] INFO consumer.TestObservable - before subscribe
//17:27:13.026 [main] INFO consumer.TestObservable - after subscribe
//17:27:13.046 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloCommand run
//17:27:13.555 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - onNext Hello kelly
//17:27:13.561 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - onCompleted

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
//17:29:32.893 [main] INFO consumer.TestObservable - before subscribe
//17:29:32.943 [main] INFO consumer.TestObservable - after subscribe
//17:29:32.971 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - HelloCommand run
//17:29:33.484 [hystrix-HelloHystrixGroup-1] INFO consumer.TestObservable - call Hello kelly
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
