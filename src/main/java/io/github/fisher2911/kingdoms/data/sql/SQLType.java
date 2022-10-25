package io.github.fisher2911.kingdoms.data.sql;

public interface SQLType {

    String getColName();

    SQLType INTEGER = () -> "INTEGER";

    SQLType DOUBLE = () -> "DOUBLE";

    SQLType BOOLEAN = () -> "BOOLEAN";

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
