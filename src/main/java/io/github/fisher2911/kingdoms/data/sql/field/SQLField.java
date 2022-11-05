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


import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.SQLType;

public class SQLField implements SQLObject {

    protected final String tableName;
    protected final String name;
    protected final SQLType type;
    protected final boolean nullable;
    protected final SQLKeyType keyType;
    protected final boolean includeInUnique;

    public SQLField(String tableName, String name, SQLType type, boolean nullable, SQLKeyType keyType, boolean includeInUnique) {
        this.tableName = tableName;
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.keyType = keyType;
        this.includeInUnique = includeInUnique;
    }

    public SQLField(String tableName, String name, SQLType type, boolean nullable, SQLKeyType keyType) {
        this(tableName, name, type, nullable, keyType, false);
    }

    public SQLField(String tableName, String name, SQLType type, SQLKeyType keyType, boolean includeInUnique) {
        this(tableName, name, type, false, keyType, includeInUnique);
    }

    public SQLField(String tableName, String name, SQLType type, SQLKeyType keyType) {
        this(tableName, name, type, false, keyType);
    }

    public SQLField(String tableName, String name, SQLType type, boolean includeInUnique) {
        this(tableName, name, type, false, SQLKeyType.NONE, includeInUnique);
    }

    public SQLField(String tableName, String name, SQLType type) {
        this(tableName, name, type, false, SQLKeyType.NONE);
    }

    public String getTableFieldStatement() {
        return this.createStatement();
    }

    public String getKeyStatement() {
        return this.getTableFieldStatement();
    }

    @Override
    public String createStatement() {
        return "`" + this.name + "` " + this.type.getColName() + (this.nullable ? "" : " NOT NULL");
    }

    public String getTableNameStatement() {
        return "`" + this.tableName + "`";
    }

    public String getTableAndName() {
        return this.tableName + ".`" + this.name + "`";
    }

    public String getAliasName() {
        return this.tableName + "_" + this.name;
    }

    public String getTableName() {
        return tableName;
    }

    public String getName() {
        return name;
    }

    public SQLType getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public SQLKeyType getKeyType() {
        return keyType;
    }

    public boolean isUnique() {
        return includeInUnique;
    }
}
