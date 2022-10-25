package io.github.fisher2911.kingdoms.data.sql.field;


import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.data.sql.SQLType;

public class SQLField implements SQLObject {

    protected final String tableName;
    protected final String name;
    protected final SQLType type;
    protected final boolean nullable;
    protected final SQLKeyType keyType;

    public SQLField(String tableName, String name, SQLType type, boolean nullable, SQLKeyType keyType) {
        this.tableName = tableName;
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.keyType = keyType;
    }

    public SQLField(String tableName, String name, SQLType type, SQLKeyType keyType) {
        this(tableName, name, type, false, keyType);
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
}
