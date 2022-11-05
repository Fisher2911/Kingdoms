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

package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UserKingdomWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.Metadata;

import java.util.ArrayList;
import java.util.List;

public enum GuiKeys {

    GUI,
    PERMISSION,
    KINGDOM,
    USER,
    ROLE_ID,
    CHUNK,
    UPGRADE_ID,
    DELETE_CONSUMER,
    INCREASE_LEVEL_CONSUMER,
    SWAP_VALUE_CONSUMER,
    MAX_LEVEL_ITEM,
    SEND_DATA_KEYS,
    PREVIOUS_MENU_ID,
    KINGDOM_MEMBER,
    USER_KINGDOM_WRAPPER,


    ;

    // I'll clean this up later - probably
    public static List<Object> toPlaceholders(Metadata metadata) {
        final List<Object> placeholders = new ArrayList<>();
        final Kingdom kingdom = metadata.get(GuiKeys.KINGDOM, Kingdom.class);
        final User user = metadata.get(GuiKeys.USER, User.class);
        final String roleId = metadata.get(GuiKeys.ROLE_ID, String.class);
        final String upgradeId = metadata.get(GuiKeys.UPGRADE_ID, String.class);
        final KPermission permission = metadata.get(GuiKeys.PERMISSION, KPermission.class);
        final User kingdomMember = metadata.get(GuiKeys.KINGDOM_MEMBER, User.class);
        final ClaimedChunk chunk = metadata.get(GuiKeys.CHUNK, ClaimedChunk.class);
        final BaseGui gui = metadata.get(GuiKeys.GUI, BaseGui.class);
        if (gui != null) placeholders.add(gui);
        if (kingdom != null) placeholders.add(kingdom);
        if (user != null) placeholders.add(user);
        if (roleId != null && kingdom != null) {
            final Role role = kingdom.getRole(roleId);
            placeholders.add(role);
        }
        if (chunk != null) {
            placeholders.add(chunk.getChunk());
        }
        if (upgradeId != null && kingdom != null) {
            final Upgrades<?> upgrades = kingdom.getUpgradeHolder().getUpgrades(upgradeId);
            final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradeId);
            if (upgradeLevel != null && upgrades != null) {
                placeholders.add(new UpgradesWrapper(upgrades, upgradeLevel));
            } else if (upgrades != null) {
                placeholders.add(upgrades);
            }
        }
        if (permission != null && kingdom != null && roleId != null) {
            final Role role = kingdom.getRole(roleId);
            if (chunk != null && chunk.getKingdomId() == kingdom.getId()) {
                placeholders.add(new PermissionWrapper(permission, chunk.hasPermission(role, permission)));
            } else if (role != null) {
                placeholders.add(new PermissionWrapper(permission, kingdom.hasPermission(role, permission)));
            }
        }
        if (kingdomMember != null && kingdom != null) {
            placeholders.add(new UserKingdomWrapper(kingdomMember, kingdom));
        } else if (user != null && kingdom != null) {
            placeholders.add(new UserKingdomWrapper(user, kingdom));
        }

        return placeholders;
    }

}
