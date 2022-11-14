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

import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UserKingdomWrapper;
import io.github.fisher2911.kingdoms.user.User;

import static io.github.fisher2911.fisherlib.gui.GuiKey.key;
import static io.github.fisher2911.fisherlib.gui.GuiKey.registerPlaceholderAccumulator;

public class GuiKeys {

    private static final Kingdoms PLUGIN = Kingdoms.getPlugin(Kingdoms.class);

    public static final GuiKey KINGDOM = key(PLUGIN, "kingdom", true);
    public static final GuiKey ROLE_ID = key(PLUGIN, "role-id", true);
    public static final GuiKey CHUNK = key(PLUGIN, "chunk", true);
    public static final GuiKey DELETE_CONSUMER = key(PLUGIN, "delete-consumer", true);
    public static final GuiKey SWAP_VALUE_CONSUMER = key(PLUGIN, "swap-value-consumer", true);
    public static final GuiKey KINGDOM_MEMBER = key(PLUGIN, "kingdom-member", true);
    public static final GuiKey USER_KINGDOM_WRAPPER = key(PLUGIN, "user-kingdom-wrapper", true);
    public static final GuiKey K_PERMISSION = key(PLUGIN, "k-permission", true);

    static {
        registerPlaceholderAccumulator(KINGDOM, ((metadata, placeholders) -> {
            final Kingdom kingdom = metadata.get(GuiKeys.KINGDOM, Kingdom.class);
            if (kingdom == null) return;
            placeholders.add(kingdom);
            final String roleId = metadata.get(GuiKeys.ROLE_ID, String.class);
            if (roleId != null) {
                final Role role = kingdom.getRole(roleId);
                placeholders.add(role);
            }
            final String upgradeId = metadata.get(GuiKey.UPGRADE_ID, String.class);
            if (upgradeId != null) {
                final Upgrades<?> upgrades = kingdom.getUpgradeHolder().getUpgrades(upgradeId);
                final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradeId);
                if (upgradeLevel != null && upgrades != null) {
                    placeholders.add(new UpgradesWrapper(upgrades, upgradeLevel));
                } else if (upgrades != null) {
                    placeholders.add(upgrades);
                }
            }
            final KPermission permission = metadata.get(GuiKeys.K_PERMISSION, KPermission.class);
            final ClaimedChunk chunk = metadata.get(GuiKeys.CHUNK, ClaimedChunk.class);
            if (permission != null && roleId != null) {
                final Role role = kingdom.getRole(roleId);
                if (chunk != null && chunk.getKingdomId() == kingdom.getId()) {
                    placeholders.add(new PermissionWrapper(permission, chunk.hasPermission(role, permission)));
                } else if (role != null) {
                    placeholders.add(new PermissionWrapper(permission, kingdom.hasPermission(role, permission)));
                }
            }

            final User user = metadata.get(GuiKey.USER, User.class);
            final User kingdomMember = metadata.get(GuiKeys.KINGDOM_MEMBER, User.class);
            if (kingdomMember != null) {
                placeholders.add(new UserKingdomWrapper(kingdomMember, kingdom));
            } else if (user != null) {
                placeholders.add(new UserKingdomWrapper(user, kingdom));
            }
        }));
        registerPlaceholderAccumulator(CHUNK, ((metadata, placeholders) -> {
            final ClaimedChunk chunk = metadata.get(GuiKeys.CHUNK, ClaimedChunk.class);
            if (chunk == null) return;
            placeholders.add(chunk);
        }));
        registerPlaceholderAccumulator(CHUNK, ((metadata, placeholders) -> {
            final ClaimedChunk chunk = metadata.get(GuiKeys.CHUNK, ClaimedChunk.class);
            if (chunk == null) return;
            placeholders.add(chunk);
        }));
        registerPlaceholderAccumulator(K_PERMISSION, ((metadata, placeholders) -> {
            final KPermission permission = metadata.get(GuiKeys.K_PERMISSION, KPermission.class);
            if (permission == null) return;
            placeholders.add(permission);
        }));
    }

}
