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
