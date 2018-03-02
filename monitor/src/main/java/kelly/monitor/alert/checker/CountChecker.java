package kelly.monitor.alert.checker;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kelly-lee on 2018/2/13.
 */
public class CountChecker {


    private static ConcurrentMap<String, AtomicInteger> countMap = Maps.newConcurrentMap();


    public enum Result {
        RECOVER, ALERT, NONE;
    }

    public static Result check(String key, boolean matchTimeExpression, int count) {
        if (matchTimeExpression) return incrAndGet(key) >= count ? Result.ALERT : Result.NONE;
        if (exists(key)) {
            clean(key);
            return Result.RECOVER;
        }
        return Result.NONE;
    }

    public static int getCount(String key) {
        return countMap.containsKey(key) ? countMap.get(key).get() : 0;
    }

    public static boolean exists(String key) {
        return countMap.containsKey(key);
    }

    public static int incrAndGet(String key) {
        if (countMap.containsKey(key))
            return countMap.get(key).incrementAndGet();
        else {
            AtomicInteger count = new AtomicInteger(1);
            countMap.put(key, count);
            return count.get();
        }
    }

    public static boolean clean(String key) {
        if (countMap.containsKey(key)) {
            countMap.remove(key);
            return true;
        }
        return false;
    }
}
