package io.github.fisher2911.kingdoms.util;

public class ArrayUtil {

    @SuppressWarnings("unchecked")
    public static <T> T[] combine(T[] first, T[] second) {
        final Object[] array = new Object[first.length + second.length];
        System.arraycopy(first, 0, array, 0, first.length);
        System.arraycopy(second, 0, array, first.length, second.length);
        return (T[]) array;
    }

}
