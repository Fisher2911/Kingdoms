package io.github.fisher2911.kingdoms.data.sql.condition;


import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhereCondition implements SQLCondition {

    private final List<Pair<SQLField, SQLObject>> conditions;

    public WhereCondition(List<Pair<SQLField, SQLObject>> conditions) {
        this.conditions = conditions;
    }

    public static WhereCondition of(SQLField field, SQLObject object) {
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

    public List<Pair<Integer, SQLObject>> getInsertionColumns() {
        final List<Pair<Integer, SQLObject>> columns = new ArrayList<>();
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

        private final List<Pair<SQLField, SQLObject>> conditions;

        private Builder() {
            this.conditions = new ArrayList<>();
        }

        public Builder addCondition(SQLField field, SQLObject value) {
            this.conditions.add(new Pair<>(field, value));
            return this;
        }

        public Builder addCondition(SQLField field, Object value) {
            this.conditions.add(Pair.of(field, SQLObject.of(value)));
            return this;
        }

        public Builder addConditions(List<Pair<SQLField, SQLObject>> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        @SafeVarargs
        public final Builder addConditions(Pair<SQLField, SQLObject>... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public WhereCondition build() {
            return new WhereCondition(this.conditions);
        }
    }
}
