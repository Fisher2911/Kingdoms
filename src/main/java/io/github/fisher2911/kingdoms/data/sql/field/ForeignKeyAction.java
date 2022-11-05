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
