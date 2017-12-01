package kelly.amazon;

/**
 * Created by kelly-lee on 17/8/26.
 */
public class A02_IsRotatedString {

    public static boolean isRotated(String s, String t) {
        if (s == null && t == null) {
            return true;
        }
        if (s == null || t == null) {
            return false;
        }
        return (s.length() == t.length()) && ((s + s).indexOf(t) != -1);
    }
}
