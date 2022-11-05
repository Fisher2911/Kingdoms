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

package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.condition.ConditionSerializer;
import io.github.fisher2911.kingdoms.config.condition.ItemConditions;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.ConditionalItem;
import io.github.fisher2911.kingdoms.gui.GuiItem;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.RolePermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import io.github.fisher2911.kingdoms.util.Metadata;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class GuiItemSerializer implements TypeSerializer<ConditionalItem> {

    public static final GuiItemSerializer INSTANCE = new GuiItemSerializer();

    private GuiItemSerializer() {
    }

    private static final String TYPE_PATH = "type";
    private static final String ITEM_PATH = "item";
    private static final String ACTIONS_PATH = "actions";
    private static final String CONDITIONALS_PATH = "conditionals";

    private static final String TYPE_PERMISSION = "permission";
    private static final String PERMISSION_PATH = "permission";

    private static final String TYPE_UPGRADE = "upgrade";
    private static final String UPGRADE_PATH = "upgrade";

    private static final String TYPE_ROLE = "role";
    private static final String ROLE_PATH = "role-id";

    @Override
    public ConditionalItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var conditionalsNode = node.node(CONDITIONALS_PATH);
        final ConditionalItem.Builder builder = ConditionalItem.builder();
        if (conditionalsNode.virtual()) {
            final BaseGuiItem item = deserializeItem(node);
            return builder.addConditionalItem(ItemConditions.alwaysTrue(ConditionalItem.of(item))).build();
        }
        for (var entry : conditionalsNode.childrenMap().entrySet()) {
            builder.addConditionalItem(ConditionSerializer.loadConditional(entry.getValue()));
        }
        return builder.build();
    }

    private static BaseGuiItem deserializeItem(ConfigurationNode node) throws SerializationException {
        final String typeString = node.node(TYPE_PATH).getString();
        final BaseItemBuilder itemBuilder = ItemSerializer.INSTANCE.deserialize(BaseItemBuilder.class, node.node(ITEM_PATH));
        final GuiItem.Builder builder = GuiItem.builder(itemBuilder);
        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers = ClickActionSerializer.deserializeAll(node.node(ACTIONS_PATH));
        builder.clickHandler(wrapper -> {
            clickHandlers.forEach(consumer -> consumer.accept(wrapper));
            wrapper.cancel();
        });
        if (typeString == null) return builder.build();
        final ConditionalItem.Builder conditionalBuilder = ConditionalItem.builder();
        switch (typeString) {
            case TYPE_PERMISSION -> GuiItemSerializer.applyPermissionItemData(conditionalBuilder, node.node(PERMISSION_PATH).getString());
            case TYPE_UPGRADE -> GuiItemSerializer.applyUpgradesItemData(
                    conditionalBuilder,
                    node.node(UPGRADE_PATH).getString(),
                    ConditionalItem.builder(deserializeItem(node.node(MAX_LEVEL_ITEM_PATH))).build()
            );
            case TYPE_ROLE -> GuiItemSerializer.applyRoleItemData(conditionalBuilder, node.node(ROLE_PATH).getString());
            default -> {
            }
        }
        return conditionalBuilder.build(builder.build()).getItem(Metadata.empty());
    }

    @Override
    public void serialize(Type type, @Nullable ConditionalItem obj, ConfigurationNode node) throws SerializationException {

    }

    private static final Consumer<InventoryEventWrapper<InventoryClickEvent>> PERMISSION_SWAP_VALUE_CLICK_ACTION =
            wrapper -> {
                final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
                final UserManager userManager = plugin.getUserManager();
                final RoleManager roleManager = plugin.getRoleManager();
                final InventoryClickEvent event = wrapper.event();
                final BaseGui gui = wrapper.gui();
                final int slot = event.getSlot();
                final BaseGuiItem clicked = gui.getBaseGuiItem(slot);
                if (clicked == null) return;
                final KPermission permission = clicked.getMetadata(GuiKeys.PERMISSION, KPermission.class);
                if (permission == null) return;
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                final Role role = roleManager.getRole(gui.getMetadata(GuiKeys.ROLE_ID, String.class), kingdom);
                final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
                event.setCancelled(true);
                final User user = userManager.forceGet(event.getWhoClicked());
                if (user == null) return;
                final Role userRole = kingdom.getRole(user);
                if (userRole == null || role == null || role.isAtLeastRank(userRole)) {
                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION, role);
                    return;
                }
                if ((chunk != null && !kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS, chunk)) ||
                        (!kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS))) {
                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
                    return;
                }

                if (chunk != null && !kingdom.hasPermission(user, permission, chunk) || (chunk == null && !kingdom.hasPermission(user, permission))) {
                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
                    return;
                }
                final boolean newValue = chunk == null ? !kingdom.hasPermission(role, permission) : !chunk.hasPermission(role, permission);
                final RolePermissionHolder permissions;
                permissions = Objects.requireNonNullElse(chunk, kingdom);
                permissions.setPermission(role, permission, newValue);
                gui.refresh(slot);
            };

    public static void applyPermissionItemData(ConditionalItem.Builder builder, String permissionId) {
        final KPermission permission = KPermission.getByName(permissionId);
        if (permission == null) return;
        applyPermissionItemData(builder, permission);
    }

    public static void applyPermissionItemData(ConditionalItem.Builder builder, KPermission permission) {
        final RoleManager roleManager = Kingdoms.getPlugin(Kingdoms.class).getRoleManager();
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.SWAP_VALUE_CONSUMER, PERMISSION_SWAP_VALUE_CLICK_ACTION);
        builder.metadata(metadata, true);
        metadata.put(GuiKeys.PERMISSION, permission);
        builder.metadata(metadata, true);
        builder.placeholder((gui, item) -> {
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            if (kingdom == null) return false;
            final Role role = roleManager.getRole(gui.getMetadata(GuiKeys.ROLE_ID, String.class), kingdom);
            if (role == null) return false;
            final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
            if (chunk == null) {
                return new PermissionWrapper(permission, kingdom.hasPermission(role, permission));
            }
            return new PermissionWrapper(permission, chunk.hasPermission(role, permission));
        });
    }

    private static final Consumer<InventoryEventWrapper<InventoryClickEvent>> UPGRADES_INCREASE_LEVEL_ACTION =
            wrapper -> {
                final KingdomManager kingdomManager = Kingdoms.getPlugin(Kingdoms.class).getKingdomManager();
                final InventoryClickEvent event = wrapper.event();
                event.setCancelled(true);
                final BaseGui gui = wrapper.gui();
                final User user = gui.getMetadata(GuiKeys.USER, User.class);
                final int slot = event.getSlot();
                final BaseGuiItem clicked = gui.getBaseGuiItem(slot);
                if (clicked == null) return;
                final String upgradeId = clicked.getMetadata(GuiKeys.UPGRADE_ID, String.class);
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                final Upgrades<?> upgrades = kingdom.getUpgradeHolder().getUpgrades(upgradeId);
                if (upgrades == null) return;
                kingdomManager.tryLevelUpUpgrade(kingdom, user, upgrades);
                final Integer newLevel = kingdom.getUpgradeLevel(upgrades.getId());
                if (newLevel != null && newLevel >= upgrades.getMaxLevel()) {
                    ConditionalItem maxLevelItem = clicked.getMetadata(GuiKeys.MAX_LEVEL_ITEM, ConditionalItem.class);
                    if (maxLevelItem == null) return;
                    gui.set(slot, maxLevelItem);
                }
                gui.refresh(slot);
            };

    @Nullable
    private static ConditionalItem getUpgradeItem(Upgrades<?> upgrades, int upgradeLevel, BaseGuiItem clicked) {
        if (upgradeLevel >= upgrades.getMaxLevel()) {
            final ConditionalItem maxLevelItem = clicked.getMetadata(GuiKeys.MAX_LEVEL_ITEM, ConditionalItem.class);
            return maxLevelItem;
        }
        return null;
    }

    public static final String MAX_LEVEL_ITEM_PATH = "max-level-item";

    public static void applyUpgradesItemData(ConditionalItem.Builder builder, String upgradeId, ConditionalItem maxLevelItem) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.INCREASE_LEVEL_CONSUMER, UPGRADES_INCREASE_LEVEL_ACTION);
        metadata.put(GuiKeys.UPGRADE_ID, upgradeId);
        metadata.put(GuiKeys.MAX_LEVEL_ITEM, maxLevelItem);
        builder.metadata(metadata, true);
    }

    public static void applyRoleItemData(ConditionalItem.Builder builder, String roleId) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.ROLE_ID, roleId);
        builder.metadata(metadata, true);
    }

    public static void applyMemberItemData(ConditionalItem.Builder builder, User user) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.KINGDOM_MEMBER, user);
        builder.metadata(metadata, true);
    }

}
