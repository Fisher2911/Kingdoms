package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLObject;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

public interface SQLStatement extends SQLObject {

    IDFinder<Integer> INTEGER_ID_FINDER = results -> results.getInt(1);

    void insert(Connection connection, List<Supplier<List<Object>>> values, int batchSize) throws SQLException;

    void insert(Connection connection, List<Supplier<List<Object>>> values) throws SQLException;

    /**
     * Creates the SQL statement.
     *
     * @return the id if auto-generated, null otherwise
     */
    @Nullable
    <ID> ID insert(Connection connection, List<Supplier<List<Object>>> values, int batchSize, IDFinder<ID> idFinder) throws SQLException;

    static InsertStatementImpl.Builder insert(String tableName) {
        return InsertStatementImpl.builder(tableName);
    }

}
