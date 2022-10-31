package io.github.fisher2911.kingdoms.config.condition;

import java.util.Objects;
import java.util.function.BiPredicate;

public enum ConditionOperation implements BiPredicate<String, String> {

    EQUALS(Objects::equals, "=="),
    NOT_EQUALS((a, b) -> !Objects.equals(a, b), "!="),
    GREATER_THAN((a, b) -> {
        if (!(a instanceof Number) || !(b instanceof Number)) {
            return false;
        }
        return ((Number) a).doubleValue() > ((Number) b).doubleValue();
    }, ">"),
    GREATER_THAN_OR_EQUALS((a, b) -> {
        if (!(a instanceof Number) || !(b instanceof Number)) {
            return false;
        }
        return ((Number) a).doubleValue() >= ((Number) b).doubleValue();
    }, ">="),
    LESS_THAN((a, b) -> {
        if (!(a instanceof Number) || !(b instanceof Number)) {
            return false;
        }
        return ((Number) a).doubleValue() < ((Number) b).doubleValue();
    }, "<"),
    LESS_THAN_OR_EQUALS((a, b) -> {
        if (!(a instanceof Number) || !(b instanceof Number)) {
            return false;
        }
        return ((Number) a).doubleValue() <= ((Number) b).doubleValue();
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
}
