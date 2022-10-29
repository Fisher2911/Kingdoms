package io.github.fisher2911.kingdoms.data.sql.table;

import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.field.ForeignKeyAction;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLForeignField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SQLTable extends SQLObject {

    String getName();
    List<SQLField> getFields();
    SQLForeignField createForeignReference(SQLField field, List<SQLField> idFields, ForeignKeyAction... actions);
    void create(Connection connection) throws SQLException;

    static SQLTableImpl.Builder builder(String name) {
        return SQLTableImpl.builder(name);
    }

}
