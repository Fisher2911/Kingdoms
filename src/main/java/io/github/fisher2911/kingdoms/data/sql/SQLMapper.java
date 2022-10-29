package io.github.fisher2911.kingdoms.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLMapper<T> {

    T map(ResultSet resultSet) throws SQLException;

}
