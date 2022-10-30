package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.condition.SQLCondition;
import io.github.fisher2911.kingdoms.data.sql.table.SQLTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteStatement implements SQLObject {

    private final String tableName;
    private final List<SQLCondition> conditions;

    private DeleteStatement(String tableName, List<SQLCondition> conditions) {
        this.tableName = tableName;
        this.conditions = conditions;
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM `").append(this.tableName).append("`");
        if (!this.conditions.isEmpty()) {
            builder.append(" WHERE ");
            int index = 0;
            for (SQLCondition condition : this.conditions) {
                builder.append(condition.createStatement());
                if (index < this.conditions.size() - 1) {
                    builder.append(" AND ");
                }
                index++;
            }
        }
        return builder.toString();
    }

    public void execute(Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement())) {
            int index = 1;
            for (var condition : this.conditions) {
                for (var insertion : condition.getInsertionColumns()) {
                    statement.setObject(index, insertion.second().get());
                    index++;
                }
            }
            statement.execute();
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<SQLCondition> getConditions() {
        return conditions;
    }

    public static Builder builder(String tableName) {
        return new Builder(tableName);
    }

    public static Builder builder(SQLTable table) {
        return builder(table.getName());
    }

    public static class Builder {

        private final String tableName;
        private final List<SQLCondition> conditions;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.conditions = new ArrayList<>();
        }

        public Builder where(SQLCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder where(SQLCondition... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public DeleteStatement build() {
            return new DeleteStatement(this.tableName, this.conditions);
        }

    }
}
