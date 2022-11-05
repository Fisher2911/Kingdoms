package io.github.fisher2911.kingdoms.data.sql;

public interface SQLType {

    String getColName();

    SQLType INTEGER = () -> "INTEGER";

    SQLType DOUBLE = () -> "DOUBLE";

    SQLType FLOAT = () -> "FLOAT";

    SQLType LONG = () -> "BIGINT";

    SQLType BOOLEAN = () -> "BOOLEAN";

    SQLType UUID = () -> "BINARY(16)";

    SQLType DATE_TIME = () -> "TIMESTAMP";

    static SQLType text(int length) {
        return () -> "TEXT(" + length + ")";
    }

    static SQLType text() {
        return () -> "TEXT";
    }

    static SQLType varchar(int length) {
        return () -> "VARCHAR(" + length + ")";
    }

    static SQLType varchar() {
        return () -> "VARCHAR";
    }

}
