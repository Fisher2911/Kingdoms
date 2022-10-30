package io.github.fisher2911.kingdoms.data.sql.condition;


import io.github.fisher2911.kingdoms.data.sql.SQLObject;
import io.github.fisher2911.kingdoms.util.Pair;

import java.util.List;
import java.util.function.Supplier;

public interface SQLCondition extends SQLObject {

    static WhereCondition.Builder where() {
        return WhereCondition.builder();
    }

    List<Pair<Integer, Supplier<Object>>> getInsertionColumns();

}
