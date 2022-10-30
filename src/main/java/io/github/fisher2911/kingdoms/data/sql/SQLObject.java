package io.github.fisher2911.kingdoms.data.sql;

public interface SQLObject {

    String createStatement();

    static SQLObject of(int i) {
        return () -> String.valueOf(i);
    }

    static SQLObject of(long l) {
        return () -> String.valueOf(l);
    }

    static SQLObject of(double d) {
        return () -> String.valueOf(d);
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
