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

package io.github.fisher2911.kingdoms.config.condition;

import io.github.fisher2911.kingdoms.util.NumberUtil;

import java.util.Objects;
import java.util.function.BiPredicate;

public enum ConditionOperation implements BiPredicate<String, String> {

    EQUALS(Objects::equals, "=="),
    NOT_EQUALS((a, b) -> !Objects.equals(a, b), "!="),
    GREATER_THAN((o1, o2) -> {
        final Double a = tryConvert(o1);
        final Double b = tryConvert(o2);
        if (a == null || b == null) return false;
        return a > b;
    }, ">"),
    GREATER_THAN_OR_EQUALS((o1, o2) -> {
        final Double a = tryConvert(o1);
        final Double b = tryConvert(o2);
        if (a == null || b == null) return false;
        return a >= b;
    }, ">="),
    LESS_THAN((o1, o2) -> {
        final Double a = tryConvert(o1);
        final Double b = tryConvert(o2);
        if (a == null || b == null) return false;
        return a < b;
    }, "<"),
    LESS_THAN_OR_EQUALS((o1, o2) -> {
        final Double a = tryConvert(o1);
        final Double b = tryConvert(o2);
        if (a == null || b == null) return false;
        return a <= b;
    }, "<=");


    private final BiPredicate<Object, Object> operation;
    private final String symbol;

    ConditionOperation(final BiPredicate<Object, Object> operation, String symbol) {
        this.operation = operation;
        this.symbol = symbol;
    }

    @Override
    public boolean test(String s, String s2) {
        return this.operation.test(s, s2);
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    private static Double tryConvert(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        return NumberUtil.doubleValueOf(String.valueOf(o));
    }
}
