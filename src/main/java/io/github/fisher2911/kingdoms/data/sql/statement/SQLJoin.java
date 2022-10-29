package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;

public class SQLJoin implements SQLObject {

    private final SQLField field;
    private final SQLField otherField;
    private final SQLJoinType type;

    public SQLJoin(SQLField field, SQLField otherField, SQLJoinType type) {
        this.field = field;
        this.otherField = otherField;
        this.type = type;
    }

    public static SQLJoin join(SQLField field, SQLField otherField, SQLJoinType type) {
        return new SQLJoin(field, otherField, type);
    }

    @Override
    public String createStatement() {
        return this.type.toString() + " `" + otherField.getTableName() + "` ON " + this.field.getTableName() + ".`" + this.field.getName() + "`=" + this.otherField.getTableName() + ".`" + this.otherField.getName() + "`";
    }
}
