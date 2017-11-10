package kelly.hystric.command;

import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HelloConfigHystrixCommand extends HystrixCommand<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloConfigHystrixCommand.class);

    private final String name;

    public HelloConfigHystrixCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloConfigHystrixGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("HelloConfigHystrixKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloConfigHystrixThreadPool"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)

                                //是否启用执行超时机制，默认为true
                                .withExecutionTimeoutEnabled(true)
                                //执行超时时间，默认为1000毫秒，
                                //如果命令是信号量隔离，则进行终止操作，因为信号量隔离与主线程是在一个线程中执行，其不会中断线程处理，所以要根据实际情况来决定是否采用信号量隔离，尤其涉及网络访问的情况。
                                .withExecutionTimeoutInMilliseconds(1000)
                                //当隔离策略为THREAD时，当执行线程执行超时时，是否进行中断处理，即Thread#interupt处理默认为true。
                                .withExecutionIsolationThreadInterruptOnTimeout(false)
                                //当隔离策略为THREAD时，当执行线程执行超时时，是否进行中断处理，即Future#cancel(true)处理，默认为false。
                                .withExecutionIsolationThreadInterruptOnFutureCancel(false) //TODO


                                //当开启了降级处理，run方法超时或者异常时将会调用getFallback处理是否开启fallback降级策略 默认:true
                                .withFallbackEnabled(true)
                                //run方法并发请求信号量配置，默认10，超出调用fallback，如果fallback没开启，抛HystrixRuntimeException：fallback execution rejected
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)
                                //fallback方法并发请求信号量配置，默认10，超出调用，抛HystrixRuntimeException：fallback execution rejected
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)


                                //是否开启请求缓存,默认:true
                                .withRequestCacheEnabled(true)
                                //是否开启请求日志,默认:true
                                .withRequestLogEnabled(true)


                                //是否开启熔断机制，默认为true
                                .withCircuitBreakerEnabled(false)
                                //是否强制关闭熔断开关，如果强制关闭了熔断开关，则请求不会被降级，一些特殊场景可以动态配置该开关，默认为false
                                .withCircuitBreakerForceClosed(false)
                                //是否强制打开熔断开关，如果强制打开可熔断开关，则请求强制降级调用getFallback处理，可以通过动态配置来打开该开关实现一些特殊需求，默认为false。
                                .withCircuitBreakerForceOpen(false)
                                //如果在一个采样时间窗口内，失败率超过该配置，则自动打开熔断开关实现降级处理，即快速失败。默认配置下采样周期为10s，失败率为50%。
                                .withCircuitBreakerErrorThresholdPercentage(50)
                                //在熔断开关闭合情况下，在进行失败率判断之前，一个采样周期内必须进行至少N个请求才能进行采样统计，目的是有足够的采样使得失败率计算正确，默认为20。
                                // 熔断器在整个统计时间内是否开启的阀值，默认20。也就是在metricsRollingStatisticalWindowInMilliseconds（默认10s）内至少请求20次，熔断器才发挥起作用
                                .withCircuitBreakerRequestVolumeThreshold(20)
                                //熔断后的重试时间窗口，且在该时间窗口内只允许一次重试。即在熔断开关打开后，在该时间窗口允许有一次重试，如果重试成功，则将重置Health采样统计并闭合熔断开关实现快速恢复，否则熔断开关还是打开状态，执行快速失败。
                                //熔断后将降级调用getFallback进行处理（fallbackEnabled=true），通过Command如下方法可以判断是否熔断了。
                                // 熔断时间窗口，默认:5秒.熔断器中断请求5秒后会进入半打开状态,放下一个请求进来重试，如果该请求成功就关闭熔断器，否则继续等待一个熔断时间窗口
                                .withCircuitBreakerSleepWindowInMilliseconds(5000)//默认为为5s


                                //该属性用来设置滚动时间窗统计指标信息时，划分"桶"的数量，默认值 10 。 metrics.rollingStats.timeInMilliseconds 参数的设置必须能被该参数整除，否则将抛出异常。
                                .withMetricsRollingStatisticalWindowBuckets(10)
                                //该属性用于设置滚动时间窗的长度，单位毫秒，该时间用于断路器判断健康度时需要收集信息的持续时间，默认值 10000 。断路器值啊收集指标信息时候会根据设置的时间窗长度拆分成多个"桶"来累计各度量值，每个"桶"记录了一段时间内的采集指标。
                                .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                                //该属性用来设置采集影响断路器状态的健康快照（请求的成功、错误百分比）的间隔等待时间，默认值 500
                                .withMetricsHealthSnapshotIntervalInMilliseconds(500)
                                //该属性用来设置在执行过程中每个"桶"中保留的最大执行次数，如果在滚动时间窗内发生超该设定值的执行次数，就从最初的位置开始重写，例如：设置为 100，滚动窗口为 10 秒，若在10秒内一个"桶"中发生了500次执行，那么该"桶"中只保留最后的100次执行的统计，默认值 100
                                .withMetricsRollingPercentileBucketSize(100)
                                //该属性用来设置对命令执行的延迟是否使用百分位数来跟踪和计算，默认值 true ，如果设置为 false 那么所有概要统计都将返回 -1
                                .withMetricsRollingPercentileEnabled(true)
                                //该属性用来设置百分位统计窗口中使用"桶"的数量，默认值 6
                                .withMetricsRollingPercentileWindowBuckets(6)
                                //该属性用来设置百分位统计的滚动窗口的持续时间，单位：毫秒，默认值 60000
                                .withMetricsRollingPercentileWindowInMilliseconds(60000)


                )
                .andThreadPoolPropertiesDefaults(// 配置线程池里的线程数
                        HystrixThreadPoolProperties.Setter()
                                //配置线程池大小,默认值10个. 建议值:请求高峰时99.5%的平均响应时间 + 向上预留一些即可
                                .withCoreSize(3)
                                //设置maximumSize启作用
                                .withAllowMaximumSizeToDivergeFromCoreSize(false)
                                //最大执行线程数 通常同coresize一样大小
                                .withMaximumSize(3)
                                //配置线程值等待队列长度,默认值:-1
                                //建议值:-1表示不等待直接拒绝,测试表明线程池使用直接决绝策略 + 合适大小的非回缩线程池效率最高.所以不建议修改此值。
                                .withMaxQueueSize(-1)
                                //当使用非回缩线程池时，queueSizeRejectionThreshold,keepAliveTimeMinutes 参数无效
                                //控制queue满后reject的threshold，因为maxQueueSize不允许热修改，因此提供这个参数可以热修改，控制队列的最大大小
                                .withQueueSizeRejectionThreshold(1000)
                                //设置线程存活多少毫秒
                                .withKeepAliveTimeMinutes(1)
                                //设置线程池统计窗口中使用"桶"的数量，默认值 10
                                .withMetricsRollingStatisticalWindowBuckets(10)
                                //设置线程池统计的滚动窗口的持续时间，单位：毫秒，默认值 10000
                                .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                )

        );
        this.name = name;
    }

    //fallback方法不能进行网络调用，应该只是缓存的数据，或者静态数据
    //如果必须走网络调用，则应该在getFallback方法中调用另一个Command实现，通过Command可以有降级和熔断机制保护应用
    @Override
    protected String getFallback() {
        return "Hello Failure " + name;
    }

    @Override
    protected String run() {
        LOGGER.info("HelloConfigHystrixCommand run");
        try {
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(5000));
        } catch (Exception e) {
            //donothing,just test blocking
        }
//            if (new Random().nextBoolean()) {
//                throw new RuntimeException("error");
//            }
        return "Hello " + name;
    }
}