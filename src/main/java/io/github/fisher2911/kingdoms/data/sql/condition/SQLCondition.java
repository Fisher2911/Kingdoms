package io.github.fisher2911.kingdoms.data.sql.condition;


import io.github.fisher2911.kingdoms.util.Pair;
import io.github.fisher2911.kingdoms.data.sql.SQLObject;

import java.util.List;

public interface SQLCondition extends SQLObject {

    static WhereCondition.Builder where() {
        return WhereCondition.builder();
    }

    List<Pair<Integer, SQLObject>> getInsertionColumns();

}
