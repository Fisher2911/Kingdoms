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

package io.github.fisher2911.kingdoms.data.sql.statement;

import io.github.fisher2911.kingdoms.data.sql.SQLObject;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

public interface SQLStatement extends SQLObject {

    IDFinder<Integer> INTEGER_ID_FINDER = results -> results.getInt(1);

    void insert(Connection connection, List<Supplier<List<Object>>> values, int batchSize) throws SQLException;

    void insert(Connection connection, List<Supplier<List<Object>>> values) throws SQLException;

    /**
     * Creates the SQL statement.
     *
     * @return the id if auto-generated, null otherwise
     */
    @Nullable
    <ID> ID insert(Connection connection, List<Supplier<List<Object>>> values, int batchSize, IDFinder<ID> idFinder) throws SQLException;

    static InsertStatementImpl.Builder insert(String tableName) {
        return InsertStatementImpl.builder(tableName);
    }

}
