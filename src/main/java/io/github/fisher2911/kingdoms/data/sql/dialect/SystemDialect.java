package io.github.fisher2911.kingdoms.data.sql.dialect;

public class SystemDialect {

    private static SQLDialect dialect = SQLDialect.SQLITE;

    public static SQLDialect getDialect() {
        return dialect;
    }

    public static void setDialect(SQLDialect dialect) {
        SystemDialect.dialect = dialect;
    }

}
