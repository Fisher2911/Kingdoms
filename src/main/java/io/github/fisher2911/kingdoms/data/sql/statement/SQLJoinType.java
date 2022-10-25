package io.github.fisher2911.kingdoms.data.sql.statement;

public enum SQLJoinType {

    INNER_JOIN,
    LEFT_JOIN,;

    @Override
    public String toString() {
        return this.name().replace("_", " ");
    }

}
