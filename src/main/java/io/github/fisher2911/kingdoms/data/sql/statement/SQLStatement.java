package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLObject;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;

public interface SQLStatement<T> extends SQLObject {

    IDFinder<Integer> INTEGER_ID_FINDER = results -> results.getInt(1);

    void insert(Connection connection) throws SQLException;

    /**
     * Creates the SQL statement.
     *
     * @return the id if auto-generated, null otherwise
     */
    @Nullable
    <ID> ID insert(Connection connection, IDFinder<ID> idFinder) throws SQLException;

    static <T> InsertStatementImpl.Builder<T> sqliteInsert(String tableName) {
        return InsertStatementImpl.builder(tableName);
    }

}
