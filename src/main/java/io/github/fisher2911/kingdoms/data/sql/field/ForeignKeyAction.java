package io.github.fisher2911.kingdoms.data.sql.field;

public enum ForeignKeyAction {

    ON_DELETE_CASCADE,
    ON_DELETE_SET_NULL,
    ON_DELETE_NO_ACTION,
    ON_DELETE_RESTRICT,
    ON_DELETE_SET_DEFAULT,
    ON_UPDATE_CASCADE,
    ON_UPDATE_SET_NULL,
    ON_UPDATE_NO_ACTION,
    ON_UPDATE_RESTRICT,
    ON_UPDATE_SET_DEFAULT;

    public String toString() {
        return super.toString().replace("_", " ");
    }
}
