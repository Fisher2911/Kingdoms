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
