/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
