package io.github.fisher2911.kingdoms.util;

public class NumberUtil {

    public static Double doubleValueOf(String s) {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer integerValueOf(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
