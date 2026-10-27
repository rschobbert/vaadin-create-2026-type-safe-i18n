package de.emesit.demo.util;

public class Strings {
    
    /**
     * Since java 8 has no isBlank method, implement functionality ourselves.
     * In later java versions just use string.isBlank().
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }
}
