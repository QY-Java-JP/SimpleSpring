package qy.util;

public class StringUtil {
    // 是否有内容
    public static boolean hasText(String s){
        if (s == null) return false;
        return !s.replaceAll(" ", "").isEmpty();
    }

}
