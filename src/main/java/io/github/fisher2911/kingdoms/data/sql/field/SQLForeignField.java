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

import java.util.Arrays;
import java.util.List;

public class SQLForeignField extends SQLField {

    private final String referencesTable;
    private final List<SQLField> references;
    private final ForeignKeyAction[] foreignKeyActions;

    public SQLForeignField(String tableName, String name, SQLType type, boolean nullable, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, name, type, nullable, SQLKeyType.FOREIGN_KEY);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    public SQLForeignField(String tableName, String name, SQLType type, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, name, type, SQLKeyType.FOREIGN_KEY);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    public SQLForeignField(String tableName, SQLField field, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, field.getName(), field.getType(), field.isNullable(), SQLKeyType.FOREIGN_KEY, field.isUnique());
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    public SQLForeignField(String tableName, String name, SQLType type, boolean nullable, boolean includeInUnique, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, name, type, nullable, SQLKeyType.FOREIGN_KEY, includeInUnique);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    public SQLForeignField(String tableName, String name, boolean includeInUnique, SQLType type, String referencesTable, List<SQLField> references, ForeignKeyAction... foreignKeyActions) {
        super(tableName, name, type, SQLKeyType.FOREIGN_KEY, includeInUnique);
        this.referencesTable = referencesTable;
        this.references = references;
        this.foreignKeyActions = foreignKeyActions;
    }

    @Override
    public String getKeyStatement() {
        return this.keyType.toString() + "(`" + this.name + "`) references " +
                this.referencesTable + "(`" + String.join(", ", this.references.stream().map(SQLField::getName).toList()) + "`) " +
                String.join(",", Arrays.stream(this.foreignKeyActions).map(ForeignKeyAction::toString).toList());
    }
}
