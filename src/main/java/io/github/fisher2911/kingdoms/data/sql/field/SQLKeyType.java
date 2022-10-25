package io.github.fisher2911.kingdoms.data.sql.field;

public enum SQLKeyType {

    FOREIGN_KEY,
    UNIQUE,
    PRIMARY_KEY,
    NONE;

    public String toString() {
        return super.toString().replace("_", " ");
    }

}
