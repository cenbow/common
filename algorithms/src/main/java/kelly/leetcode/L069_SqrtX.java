package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/18.
 */
public class L069_SqrtX {

    public int mySqrt(int x) {
        long left = 0;
        long right = (long) x + 1;
        long ans = 0;
        while (left < right) {
            long mid = left + (right - left) / 2;
            if (guess(mid, x)) {
                ans = mid;
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return (int) ans;
    }

    boolean guess(long i, long x) {
        return i * i <= x;
    }

}
