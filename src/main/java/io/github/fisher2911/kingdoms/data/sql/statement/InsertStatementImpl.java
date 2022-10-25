package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.util.Pair;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertStatementImpl<T> implements SQLStatement<T> {

    private final String tableName;
    private final List<Pair<String, Object>> values;

    private InsertStatementImpl(String tableName, List<Pair<String, Object>> values) {
        this.tableName = tableName;
        this.values = values;
    }

    @Override
    public String createStatement() {
        return "INSERT OR REPLACE INTO " +
                this.tableName + " (" +
                this.getFieldsString() + ") VALUES (" +
                this.getValuesString() + ")";
    }

    private String getFieldsString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var pair : this.values) {
            builder.append(pair.first());
            if (index < this.values.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    private String getValuesString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var pair : this.values) {
            builder.append("?");
            if (index < this.values.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    @Override
    public <ID> ID insert(Connection connection, IDFinder<ID> idFinder) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement(), Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (var pair : this.values) {
                statement.setObject(index, pair.second());
                index++;
            }
            statement.executeUpdate();
            if (idFinder == null) return null;
            final ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) return idFinder.find(keys);
            return null;
        }
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        this.insert(connection, null);
    }

    protected static <T> Builder<T> builder(String tableName) {
        return new Builder<T>(tableName);
    }

    public static class Builder<T> {

        private final String tableName;
        private final List<Pair<String, Object>> values;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.values = new ArrayList<>();
        }

        public Builder<T> add(SQLField field, Object value) {
            this.values.add(new Pair<>(field.getName(), value));
            return this;
        }

        public Builder<T> addAll(List<Pair<SQLField, Object>> values) {
            for (var pair : values) {
                this.add(pair.first(), pair.second());
            }
            return this;
        }

        @SafeVarargs
        public final Builder<T> addAll(Pair<SQLField, Object>... values) {
            return this.addAll(Arrays.asList(values));
        }

        public InsertStatementImpl<T> build() {
            return new InsertStatementImpl<T>(this.tableName, this.values);
        }
    }
}
