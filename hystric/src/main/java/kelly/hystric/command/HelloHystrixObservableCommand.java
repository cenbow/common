package kelly.hystric.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

//实例可以顺序发送多条数据
//没有execute()和queue()
public class HelloHystrixObservableCommand extends HystrixObservableCommand<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloHystrixObservableCommand.class);
    private final String name;

    public HelloHystrixObservableCommand(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("HelloHystrixObservableGroup"));
        this.name = name;
    }

    public Observable<String> resumeWithFallback() {
        return Observable.just("Hi Failure " + name);
    }
    //程序线程执行
    @Override
    protected Observable<String> construct() {
        LOGGER.info("HelloHystrixObservableCommand construct");
        return Observable.create(new Observable.OnSubscribe<String>() {
            public void call(Subscriber<? super String> observer) {
                try {
                    if (!observer.isUnsubscribed()) {
                        // observer.onNext("Hello " + name + "!");
                        if(true)
                        throw new RuntimeException("aa");
                        observer.onNext("Hi " + name + "!");
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}