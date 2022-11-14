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

import io.github.fisher2911.fisherlib.config.serializer.GuiItemSerializer;
import io.github.fisher2911.fisherlib.config.serializer.ItemSerializers;
import io.github.fisher2911.fisherlib.configurate.ConfigurationNode;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.BaseGuiItem;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.fisherlib.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.RolePermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class KingdomsGuiItemSerializer extends GuiItemSerializer<User, Kingdoms> {

    public static final KingdomsGuiItemSerializer INSTANCE = new KingdomsGuiItemSerializer(Kingdoms.getPlugin(Kingdoms.class));

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

    private KingdomsGuiItemSerializer(Kingdoms plugin) {
        super(plugin);
        this.registerDataApplier(TYPE_PERMISSION, this::applyPermissionItemData);
        this.registerDataApplier(TYPE_ROLE, this::applyRoleItemData);
//        KingdomsGuiItemSerializer.applyPermissionItemData(conditionalBuilder, node.node(PERMISSION_PATH).getString()
    }

//    @Override
//    public ConditionalItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
//        final var conditionalsNode = node.node(CONDITIONALS_PATH);
//        final ConditionalItem.Builder builder = ConditionalItem.builder();
//        if (conditionalsNode.virtual()) {
//            final BaseGuiItem item = deserializeItem(node);
//            return builder.addConditionalItem(ItemConditions.alwaysTrue(ConditionalItem.of(item))).build();
//        }
//        for (var entry : conditionalsNode.childrenMap().entrySet()) {
//            builder.addConditionalItem(ConditionSerializer.loadConditional(entry.getValue()));
//        }
//        return builder.build();
//    }

//    private static BaseGuiItem deserializeItem(ConfigurationNode node) throws SerializationException {
//        final String typeString = node.node(TYPE_PATH).getString();
//        final BaseItemBuilder itemBuilder = ItemSerializer.INSTANCE.deserialize(BaseItemBuilder.class, node.node(ITEM_PATH));
//        final GuiItem.Builder builder = GuiItem.builder(itemBuilder);
//        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers = ClickActionSerializer.deserializeAll(node.node(ACTIONS_PATH));
//        builder.clickHandler(wrapper -> {
//            clickHandlers.forEach(consumer -> consumer.accept(wrapper));
//            wrapper.cancel();
//        });
//        if (typeString == null) return builder.build();
//        final ConditionalItem.Builder conditionalBuilder = ConditionalItem.builder();
//        switch (typeString) {
//            case TYPE_PERMISSION -> KingdomsGuiItemSerializer.applyPermissionItemData(conditionalBuilder, node.node(PERMISSION_PATH).getString());
//            case TYPE_UPGRADE -> KingdomsGuiItemSerializer.applyUpgradesItemData(
//                    conditionalBuilder,
//                    node.node(UPGRADE_PATH).getString(),
//                    ConditionalItem.builder(deserializeItem(node.node(MAX_LEVEL_ITEM_PATH))).build()
//            );
//            case TYPE_ROLE -> KingdomsGuiItemSerializer.applyRoleItemData(conditionalBuilder, node.node(ROLE_PATH).getString());
//            default -> {
//            }
//        }
//        return conditionalBuilder.build(builder.build()).getItem(Metadata.empty());
//    }
//
//    @Override
//    public void serialize(Type type, @Nullable ConditionalItem obj, ConfigurationNode node) throws SerializationException {
//
//    }

    private static final Consumer<InventoryEventWrapper<InventoryClickEvent>> PERMISSION_SWAP_VALUE_CLICK_ACTION =
            wrapper -> {
                final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
                final MessageHandler messageHandler = plugin.getMessageHandler();
                final UserManager userManager = plugin.getUserManager();
                final RoleManager roleManager = plugin.getRoleManager();
                final InventoryClickEvent event = wrapper.event();
                final BaseGui gui = wrapper.gui();
                final int slot = event.getSlot();
                final BaseGuiItem clicked = gui.getBaseGuiItem(slot);
                if (clicked == null) return;
                final KPermission permission = clicked.getMetadata(GuiKeys.K_PERMISSION, KPermission.class);
                if (permission == null) return;
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                final Role role = roleManager.getRole(gui.getMetadata(GuiKeys.ROLE_ID, String.class), kingdom);
                final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
                event.setCancelled(true);
                final User user = userManager.forceGet(event.getWhoClicked());
                if (user == null) return;
                final Role userRole = kingdom.getRole(user);
                if (userRole == null || role == null || role.isAtLeastRank(userRole)) {
                    messageHandler.sendMessage(user, KMessage.CANNOT_EDIT_KINGDOM_PERMISSION, role);
                    return;
                }
                if ((chunk != null && !kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS, chunk)) ||
                        (!kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS))) {
                    messageHandler.sendMessage(user, KMessage.CANNOT_EDIT_KINGDOM_PERMISSION);
                    return;
                }

                if (chunk != null && !kingdom.hasPermission(user, permission, chunk) || (chunk == null && !kingdom.hasPermission(user, permission))) {
                    messageHandler.sendMessage(user, KMessage.CANNOT_EDIT_KINGDOM_PERMISSION);
                    return;
                }
                final boolean newValue = chunk == null ? !kingdom.hasPermission(role, permission) : !chunk.hasPermission(role, permission);
                final RolePermissionHolder permissions;
                permissions = Objects.requireNonNullElse(chunk, kingdom);
                permissions.setPermission(role, permission, newValue);
                gui.refresh(slot);
            };

    public void applyPermissionItemData(ConditionalItem.Builder builder, ConfigurationNode node, ItemSerializers<User, Kingdoms> serializers) {
        this.applyPermissionItemData(builder, node.node(PERMISSION_PATH).getString());
    }

    public void applyPermissionItemData(ConditionalItem.Builder builder, String permissionId) {
        final KPermission permission = KPermission.getByName(permissionId);
        if (permission == null) return;
        applyPermissionItemData(builder, permission);
    }

    public void applyPermissionItemData(ConditionalItem.Builder builder, KPermission permission) {
        final RoleManager roleManager = Kingdoms.getPlugin(Kingdoms.class).getRoleManager();
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.SWAP_VALUE_CONSUMER, PERMISSION_SWAP_VALUE_CLICK_ACTION);
        builder.metadata(metadata, true);
        metadata.put(GuiKeys.K_PERMISSION, permission);
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

    public void applyRoleItemData(ConditionalItem.Builder builder, ConfigurationNode node, ItemSerializers<User, Kingdoms> serializers) {
        this.applyRoleItemData(builder, node.node(ROLE_PATH).getString());
    }

    public void applyRoleItemData(ConditionalItem.Builder builder, String roleId) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.ROLE_ID, roleId);
        builder.metadata(metadata, true);
    }

    public void applyMemberItemData(ConditionalItem.Builder builder, User user) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.KINGDOM_MEMBER, user);
        builder.metadata(metadata, true);
    }

}
