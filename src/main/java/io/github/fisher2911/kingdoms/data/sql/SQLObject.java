package io.github.fisher2911.kingdoms.data.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLObject {

    String createStatement();

    static SQLObject of(int i) {
        return () -> String.valueOf(i);
    }

    static SQLObject of(boolean b) {
        return () -> String.valueOf(b);
    }

    static SQLObject of(String s) {
        return () -> "'" + s + "'";
    }

    static SQLObject of(Object o) {
        return () -> String.valueOf(o);
    }

}
