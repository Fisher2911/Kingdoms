package io.github.fisher2911.kingdoms.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EnumUtil {

    @Nullable
    public static <E extends Enum<E>> E valueOf(Class<E> clazz, String str) {
        try {
            return Enum.valueOf(clazz, str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <E extends Enum<E>> E stringToEnum(String enumAsString,
                                                     Class<E> enumClass,
                                                     E defaultEnum) {
        return stringToEnum(enumAsString, enumClass, defaultEnum, e -> {
        });
    }

    /**
     * @param enumAsString Enum value as a string to be parsed
     * @param enumClass    enum type enumAsString is to be converted to
     * @param defaultEnum  default value to be returned
     * @param consumer     accepts the returned enum, can be used for logging
     * @return enumAsString as an enum, or default enum if it could not be parsed
     */

    public static <E extends Enum<E>> E stringToEnum(String enumAsString,
                                                     final Class<E> enumClass,
                                                     E defaultEnum,
                                                     Consumer<E> consumer) {
        try {
            final E value = Enum.valueOf(enumClass, enumAsString);
            consumer.accept(value);
            return value;
        } catch (final IllegalArgumentException exception) {
            consumer.accept(defaultEnum);
            return defaultEnum;
        }
    }

}
