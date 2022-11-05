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

package io.github.fisher2911.kingdoms.data.sql.field;

import io.github.fisher2911.kingdoms.data.sql.SQLType;
import io.github.fisher2911.kingdoms.data.sql.dialect.SQLDialect;
import io.github.fisher2911.kingdoms.data.sql.dialect.SystemDialect;;

public class SQLIdField extends SQLField {

    private final boolean autoIncrement;

    public SQLIdField(String tableName, String name, SQLType type, boolean nullable, SQLKeyType keyType, boolean autoIncrement) {
        super(tableName, name, type, nullable, keyType);
        this.autoIncrement = autoIncrement;
    }

    public SQLIdField(String tableName, String name, SQLType type, SQLKeyType keyType, boolean autoIncrement) {
        this(tableName, name, type, false, keyType, autoIncrement);
    }

    @Override
    public String createStatement() {
        final String keyString = this.keyType == SQLKeyType.PRIMARY_KEY && SystemDialect.getDialect() == SQLDialect.MYSQL ? " " + this.keyType : "";
        final String autoIncrementString = this.keyType == SQLKeyType.PRIMARY_KEY && this.autoIncrement && SystemDialect.getDialect() == SQLDialect.MYSQL ? " AUTOINCREMENT" : "";
        return super.createStatement() + keyString + autoIncrementString;
    }
}
