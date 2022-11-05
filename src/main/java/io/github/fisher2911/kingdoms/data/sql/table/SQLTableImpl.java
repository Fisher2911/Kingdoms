/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.data.sql.table;


import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.data.sql.dialect.SQLDialect;
import io.github.fisher2911.kingdoms.data.sql.dialect.SystemDialect;
import io.github.fisher2911.kingdoms.data.sql.field.ForeignKeyAction;
import io.github.fisher2911.kingdoms.data.sql.field.SQLField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLForeignField;
import io.github.fisher2911.kingdoms.data.sql.field.SQLKeyType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SQLTableImpl implements SQLTable {

    private final String name;
    private final List<SQLField> fields;
    private final Multimap<SQLKeyType, SQLField> keys;

    private SQLTableImpl(String name, List<SQLField> fields) {
        this.name = name;
        this.fields = fields;
        this.keys = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (SQLField field : fields) {
            this.keys.put(field.getKeyType(), field);
            if (field.isUnique() && field.getKeyType() != SQLKeyType.UNIQUE) {
                this.keys.put(SQLKeyType.UNIQUE, field);
            }
        }
    }

    @Override
    public String createStatement() {
        final StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(this.name).append(" (");
        int index = 0;
        for (var field : this.fields) {
            builder.append(field.createStatement());
            if (index < this.fields.size() - 1) {
                builder.append(", ");
            }
            index++;
        }
        for (var key : this.keys.keySet()) {
            if (key == SQLKeyType.NONE) continue;
            if (key == SQLKeyType.PRIMARY_KEY && SystemDialect.getDialect() == SQLDialect.MYSQL) continue;
            builder.append(", ");
            if (key != SQLKeyType.FOREIGN_KEY) builder.append(key.toString()).append("(");
            int keyIndex = 0;
            for (var field : this.keys.get(key)) {
                if (key == SQLKeyType.FOREIGN_KEY) {
                    builder.append(field.getKeyStatement());
                } else {
                    builder.append("`").append(field.getName()).append("`");
                }
                if (keyIndex < this.keys.get(key).size() - 1) {
                    builder.append(", ");
                }
                keyIndex++;
            }
            if (key != SQLKeyType.FOREIGN_KEY) builder.append(")");
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void create(Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(this.createStatement());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<SQLField> getFields() {
        return this.fields;
    }

    @Override
    public SQLForeignField createForeignReference(SQLField field, List<SQLField> idFields, ForeignKeyAction... actions) {
        return new SQLForeignField(field.getTableName(), field, this.name, idFields, actions);
    }

    protected static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;
        private final List<SQLField> fields = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        public Builder addField(SQLField field) {
            this.fields.add(field);
            return this;
        }

        public Builder addFields(List<SQLField> fields) {
            this.fields.addAll(fields);
            return this;
        }

        public Builder addFields(SQLField... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public SQLTableImpl build() {
            return new SQLTableImpl(this.name, this.fields);
        }
    }
}
