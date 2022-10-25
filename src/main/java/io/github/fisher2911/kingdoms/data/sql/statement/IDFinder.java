package io.github.fisher2911.kingdoms.data.sql.statement;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDFinder<ID> {

    ID find(ResultSet resultSet) throws SQLException;

}
