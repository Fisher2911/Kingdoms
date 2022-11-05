package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.dialect.SQLDialect;
import io.github.fisher2911.kingdoms.data.sql.dialect.SystemDialect;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.util.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class InsertStatementImpl implements SQLStatement {

    private final String tableName;
    private final List<String> fields;

    private InsertStatementImpl(String tableName, List<String> fields) {
        this.tableName = tableName;
        this.fields = fields;
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
        for (var field : this.fields) {
            builder.append(field);
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    private String getValuesString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var field : this.fields) {
            builder.append("?");
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        return builder.toString();
    }

    @Override
    public <ID> ID insert(Connection connection, List<Supplier<List<Object>>> values, int batchSize, IDFinder<ID> idFinder) throws SQLException {
        try (var statement = connection.prepareStatement(this.createStatement(), Statement.RETURN_GENERATED_KEYS)) {
            int batches = 0;
            if (SystemDialect.getDialect() == SQLDialect.SQLITE) {
                connection.setAutoCommit(false);
            }
            for (Supplier<List<Object>> list : values) {
                int index = 1;
                for (Object object : list.get()) {
                    statement.setObject(index, object);
                    index++;
                }
                if (batchSize > 0 && batches < batchSize) {
                    statement.addBatch();
                    batches++;
                } else {
                    statement.executeBatch();
                    batches = 0;
                }
            }
            statement.executeBatch();
            connection.commit();
            if (idFinder == null) return null;
            final ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) return idFinder.find(keys);
            return null;
        }
    }

    @Override
    public void insert(Connection connection, List<Supplier<List<Object>>> values) throws SQLException {
        this.insert(connection, values, 1, null);
    }

    @Override
    public void insert(Connection connection, List<Supplier<List<Object>>> values, int batchSize) throws SQLException {
        this.insert(connection, values, batchSize, null);
    }

    protected static Builder builder(String tableName) {
        return new Builder(tableName);
    }

    public static class Builder {

        private final String tableName;
        private final List<String> fields;

        private Builder(String tableName) {
            this.tableName = tableName;
            this.fields = new ArrayList<>();
        }

        public Builder add(SQLField field) {
            this.fields.add(field.getName());
            return this;
        }

        public Builder addAll(List<Pair<SQLField, Object>> values) {
            for (var pair : values) {
                this.fields.add(pair.first().getName());
            }
            return this;
        }

        @SafeVarargs
        public final Builder addAll(Pair<SQLField, Object>... values) {
            return this.addAll(Arrays.asList(values));
        }

        public InsertStatementImpl build() {
            return new InsertStatementImpl(this.tableName, this.fields);
        }
    }
}
