package io.github.fisher2911.kingdoms.gui;

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

    public static List<Object> toPlaceholders(Metadata metadata) {
        final List<Object> placeholders = new ArrayList<>();
        final Kingdom kingdom = metadata.get(GuiKeys.KINGDOM, Kingdom.class);
        final User user = metadata.get(GuiKeys.USER, User.class);
        final String roleId = metadata.get(GuiKeys.ROLE_ID, String.class);
        final String upgradeId = metadata.get(GuiKeys.UPGRADE_ID, String.class);
        final KPermission permission = metadata.get(GuiKeys.PERMISSION, KPermission.class);
        final User kingdomMember = metadata.get(GuiKeys.KINGDOM_MEMBER, User.class);
        final BaseGui gui = metadata.get(GuiKeys.GUI, BaseGui.class);
        if (gui != null) placeholders.add(gui);
        if (kingdom != null) placeholders.add(kingdom);
        if (user != null) placeholders.add(user);
        if (roleId != null && kingdom != null) {
            final Role role = kingdom.getRole(roleId);
            placeholders.add(role);
        }
        if (upgradeId != null && kingdom != null) {
            final Upgrades<?> upgrades = kingdom.getUpgradeHolder().getUpgrades(upgradeId);
            final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradeId);
            if (upgradeLevel != null && upgrades != null) {
                placeholders.add(new UpgradesWrapper(upgrades, upgradeLevel));
            } else if (upgrades != null) {
                placeholders.add(upgrades);
            }
//            if (upgrades != null) {
//                placeholders.add(new UpgradeLevelWrapper(kingdom, upgradeId));
//            }
        }
        if (permission != null && kingdom != null && roleId != null) {
            final Role role = kingdom.getRole(roleId);
            if (role != null)
                placeholders.add(new PermissionWrapper(permission, kingdom.hasPermission(role, permission)));
        }
        if (kingdomMember != null && kingdom != null) {
            placeholders.add(new UserKingdomWrapper(kingdomMember, kingdom));
        } else if (user != null && kingdom != null) {
            placeholders.add(new UserKingdomWrapper(user, kingdom));
        }

        return placeholders;
    }

}
