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

package io.github.fisher2911.kingdoms.kingdom.upgrade;

public enum UpgradeId {

    MAX_CLAIMS("Max Claims"),
    MAX_SPAWNERS("Max Spawners"),
    MAX_HOPPERS_PER_CHUNK("Max Hoppers Per Chunk"),
    MAX_MEMBERS("Max Members"),
    BANK_LIMIT("Bank Limit"),
    MAX_ALLIES("Max Allies"),
    MAX_TRUCES("Max Truces"),
    MAX_ENEMIES("Max Enemies"),

    ;

    private final String displayName;

    UpgradeId(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "-");
    }
}
