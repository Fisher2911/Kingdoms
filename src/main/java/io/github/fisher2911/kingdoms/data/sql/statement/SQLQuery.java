package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLMapper;
import io.github.fisher2911.kingdoms.data.sql.SQLObject;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLQuery<T> extends SQLObject {

    T mapTo(Connection connection, SQLMapper<T> mapper) throws SQLException;

    static <T> SelectStatementImpl.Builder<T> select(String tableName) {
        return SelectStatementImpl.builder(tableName);
    }

}
