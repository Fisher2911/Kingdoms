package io.github.fisher2911.kingdoms.data.sql.condition;


import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class WhereCondition implements SQLCondition {

    private final List<Pair<SQLField, Supplier<Object>>> conditions;

    public WhereCondition(List<Pair<SQLField, Supplier<Object>>> conditions) {
        this.conditions = conditions;
    }

    public static WhereCondition of(SQLField field, Supplier<Object> object) {
        return new WhereCondition(List.of(new Pair<>(field, object)));
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var pair : this.conditions) {
            builder.append(pair.first().getTableAndName()).append(" = (?)");/*.append(pair.second().createStatement()).append(")");*/
            if (index < this.conditions.size() - 1) {
                builder.append(" AND ");
            }
            index++;
        }
        return builder.toString();
    }

    public List<Pair<Integer, Supplier<Object>>> getInsertionColumns() {
        final List<Pair<Integer, Supplier<Object>>> columns = new ArrayList<>();
        int index = 1;
        for (var pair : this.conditions) {
            columns.add(Pair.of(index, pair.second()));
        }
        return columns;
    }

    protected static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Pair<SQLField, Supplier<Object>>> conditions;

        private Builder() {
            this.conditions = new ArrayList<>();
        }

        public Builder addCondition(SQLField field, Supplier<Object> value) {
            this.conditions.add(new Pair<>(field, value));
            return this;
        }

        public Builder addCondition(SQLField field, Object value) {
            this.conditions.add(Pair.of(field, () -> value));
            return this;
        }

        public Builder addConditions(List<Pair<SQLField, Supplier<Object>>> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        @SafeVarargs
        public final Builder addConditions(Pair<SQLField, Supplier<Object>>... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public WhereCondition build() {
            return new WhereCondition(this.conditions);
        }
    }
}
