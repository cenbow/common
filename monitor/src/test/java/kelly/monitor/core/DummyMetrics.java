package kelly.monitor.core;

import com.google.common.base.Strings;

public class DummyMetrics {

    public static void recordError(String metricName) {
        errorCount(metricName).inc();
    }

    public static void recordErrorWithTag(String metricName, String tagKey, String tagValue) {
        errorCount(metricName, tagKey, tagValue).inc();
    }


    public static Counter errorCount(String metricName) {
        return errorCount(metricName, null, null);
    }

    public static Counter errorCount(String metricName, String tagKey, String tagValue) {
        DeltaKeyWrapper<Counter> deltaKeyWrapper = Metrics.counter(metricName).delta();
        if (Strings.isNullOrEmpty(tagKey) || Strings.isNullOrEmpty(tagValue)) {
            return deltaKeyWrapper.get();
        } else {
            return deltaKeyWrapper.tag(tagKey, tagValue).get();
        }
    }



    public static void recordQPS(String metricName) {
        qps(metricName).mark();
    }


    public static void recordQPSWithTag(String metricName, String tagKey, String tagValue) {
        qps(metricName, tagKey, tagValue).mark();
    }

    public static Meter qps(String metricName) {
        return qps(metricName, null, null);
    }

    public static Meter qps(String metricName, String tagKey, String tagValue) {
        KeyWrapper<Meter> meterKeyWrapper = Metrics.meter(metricName);
        if (Strings.isNullOrEmpty(tagKey) || Strings.isNullOrEmpty(tagValue)) {
            return meterKeyWrapper.get();
        } else {
            return meterKeyWrapper.tag(tagKey, tagValue).get();
        }
    }

}
