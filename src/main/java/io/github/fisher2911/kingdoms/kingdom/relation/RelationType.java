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

package io.github.fisher2911.kingdoms.kingdom.relation;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.fisherlib.util.StringUtils;

import java.util.function.BiFunction;

public enum RelationType {

    ENEMY((kingdom, manager) -> manager.getEnemyRole(kingdom), 0, KPermission.ADD_ENEMY, KPermission.REMOVE_ENEMY, true),
    NEUTRAL((kingdom, manager) -> manager.getNeutralRole(kingdom), 1, KPermission.ADD_NEUTRAL, KPermission.REMOVE_NEUTRAL, true),
    TRUCE((kingdom, manager) -> manager.getTruceRole(kingdom), 2, KPermission.ADD_TRUCE, KPermission.REMOVE_TRUCE, false),
    ALLY((kingdom, manager) -> manager.getAllyRole(kingdom), 3, KPermission.ADD_ALLY, KPermission.REMOVE_ALLY, false);

    private final BiFunction<Kingdom, RoleManager, Role> getRole;
    private final int weight;
    private final KPermission addPermission;
    private final KPermission removePermission;
    private final boolean canBeOneWay;

    RelationType(BiFunction<Kingdom, RoleManager, Role> getRole, int weight, KPermission addPermission, KPermission removePermission, boolean canBeOneWay) {
        this.getRole = getRole;
        this.weight = weight;
        this.addPermission = addPermission;
        this.removePermission = removePermission;
        this.canBeOneWay = canBeOneWay;
    }

    public String displayName() {
        return StringUtils.capitalize(this.toString().toLowerCase());
    }

    public Role getRole(Kingdom kingdom, RoleManager roleManager) {
        return this.getRole.apply(kingdom, roleManager);
    }

    public int getWeight() {
        return weight;
    }

    public boolean isLowerThan(RelationType other) {
        return this.weight < other.weight;
    }

    public KPermission getAddPermission() {
        return addPermission;
    }

    public KPermission getRemovePermission() {
        return removePermission;
    }

    public boolean canBeOneWay() {
        return canBeOneWay;
    }
}
