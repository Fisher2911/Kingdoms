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

package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLMapper;
import io.github.fisher2911.kingdoms.data.sql.condition.SQLCondition;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectStatementImpl<T> implements SQLQuery<T> {

    private final String tableName;
    private final List<SQLField> fields;
    private final List<SQLCondition> conditions;
    private final List<SQLJoin> joins;

    private SelectStatementImpl(
            String tableName,
            List<SQLField> fields,
            List<SQLCondition> conditions,
            List<SQLJoin> joins
    ) {
        this.tableName = tableName;
        this.fields = fields;
        this.conditions = conditions;
        this.joins = joins;
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder("SELECT ").
                append(this.getFieldsString()).append(" FROM ").
                append(this.tableName);
        for (var join : this.joins) {
            builder.append(" ").append(join.createStatement());
        }
        if (!this.conditions.isEmpty()) {
            builder.append(" WHERE ");
            int index = 0;
            for (var condition : this.conditions) {
                builder.append(condition.createStatement());
                if (index < this.conditions.size() - 1) {
                    builder.append(" AND ");
                }
                index++;
            }
        }
        return builder.toString();
    }

    private String getFieldsString() {
        if (fields.isEmpty()) return "*";
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (SQLField field : this.fields) {
            builder.append(field.getTableAndName());
            if (!this.joins.isEmpty()) {
                builder.append(" AS ").append(field.getAliasName());
            }
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    public T mapTo(Connection connection, SQLMapper<T> mapper) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement())) {
            int currentIndex = 0;
            for (var condition : this.conditions) {
                for (var insertion : condition.getInsertionColumns()) {
                    currentIndex++;
                    statement.setObject(currentIndex, insertion.second().get());
                }
            }
            return mapper.map(statement.executeQuery());
        }
    }

    protected static <T> Builder<T> builder(String tableName) {
        return new Builder<>(tableName);
    }

    public static class Builder<T> {

        private final String tableName;
        private final List<SQLField> fields;
        private final List<SQLCondition> conditions;
        private final List<SQLJoin> joins;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.fields = new ArrayList<>();
            this.conditions = new ArrayList<>();
            this.joins = new ArrayList<>();
        }

        public Builder<T> select(SQLField... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder<T> where(SQLCondition... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public Builder<T> join(SQLJoin... joins) {
            this.joins.addAll(Arrays.asList(joins));
            return this;
        }

        public Builder<T> join(SQLField thisField, SQLField otherField, SQLJoinType type) {
            this.joins.add(new SQLJoin(thisField, otherField, type));
            return this;
        }

        public SelectStatementImpl<T> build() {
            return new SelectStatementImpl<>(this.tableName, this.fields, this.conditions, this.joins);
        }
    }
}
