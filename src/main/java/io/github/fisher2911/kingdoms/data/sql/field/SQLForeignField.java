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
