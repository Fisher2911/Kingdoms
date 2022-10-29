package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
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
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradeLevelWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
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
import java.util.stream.Collectors;

public class GuiItemSerializer implements TypeSerializer<BaseGuiItem> {

    public static final GuiItemSerializer INSTANCE = new GuiItemSerializer();

    private GuiItemSerializer() {
    }

    private static final String TYPE_PATH = "type";
    private static final String ITEM_PATH = "item";
    private static final String ACTIONS_PATH = "actions";

//    private static final Map<String, Supplier<Map<Object, Object>>> metadataSupplier = new HashMap<>();


    private static final String TYPE_PERMISSION = "permission";
    private static final String PERMISSION_PATH = "permission";

    private static final String TYPE_UPGRADE = "upgrade";
    private static final String UPGRADE_PATH = "upgrade";

    private static final String TYPE_ROLE = "role";
    private static final String ROLE_PATH = "role-id";
//    static {
//        metadataSupplier.put(PERMISSION_TYPE, PermissionItemSerializer::getPermissionItemMetaData);
//    }

    @Override
    public BaseGuiItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final String typeString = node.node(TYPE_PATH).getString();
        final ItemBuilder itemBuilder = ItemSerializer.INSTANCE.deserialize(ItemBuilder.class, node.node(ITEM_PATH));
        final GuiItem.Builder builder = GuiItem.builder(itemBuilder);
        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers = ClickActionSerializer.deserializeAll(node.node(ACTIONS_PATH));
        builder.clickHandler(wrapper -> {
            clickHandlers.forEach(consumer -> consumer.accept(wrapper));
            wrapper.cancel();
        });
        if (typeString == null) return builder.build();
        switch (typeString) {
            case TYPE_PERMISSION -> GuiItemSerializer.applyPermissionItemData(builder, node.node(PERMISSION_PATH).getString());
            case TYPE_UPGRADE -> GuiItemSerializer.applyUpgradesItemData(
                    builder,
                    node.node(UPGRADE_PATH).getString(),
                    GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, node.node(MAX_LEVEL_ITEM_PATH))
            );
            case TYPE_ROLE -> GuiItemSerializer.applyRoleItemData(builder, node.node(ROLE_PATH).getString());
            default -> {
            }
        }
        return builder.build();
    }

    @Override
    public void serialize(Type type, @Nullable BaseGuiItem obj, ConfigurationNode node) throws SerializationException {

    }

    private static final Consumer<InventoryEventWrapper<InventoryClickEvent>> PERMISSION_SWAP_VALUE_CLICK_ACTION =
            wrapper -> {
                final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
                final UserManager userManager = plugin.getUserManager();
                final RoleManager roleManager = plugin.getRoleManager();
                final InventoryClickEvent event = wrapper.event();
                final BaseGui gui = wrapper.gui();
                final int slot = event.getSlot();
                final BaseGuiItem clicked = gui.getItem(slot);
                if (clicked == null) return;
                final KPermission permission = clicked.getMetadata(GuiKeys.PERMISSION, KPermission.class);
                if (permission == null) return;
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                final Role role = roleManager.getRole(gui.getMetadata(GuiKeys.ROLE_ID, String.class), kingdom);
                System.out.println("Kingdom roles: " + kingdom.getRoles());
                System.out.println("role: " + gui.getMetadata().keySet().stream().map(o -> String.valueOf(o) + " - " + o.getClass().getName()).collect(Collectors.toList()));
                System.out.println("Clicked GUI metadata: " + gui.getMetadata());
                final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
                event.setCancelled(true);
                final User user = userManager.wrap(event.getWhoClicked());
                final Role userRole = kingdom.getRole(user);
                if (userRole == null || role == null || role.isAtLeastRank(userRole)) {
                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION, role);
                    return;
                }
                if ((chunk != null && !kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS, chunk)) &&
                        (!kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS))) {
                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
                    return;
                }

                if (chunk != null && !kingdom.hasPermission(user, permission, chunk) || (chunk == null && !kingdom.hasPermission(user, permission))) {
                    MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
                    return;
                }
                final boolean newValue = chunk == null ? !kingdom.hasPermission(role, permission) : !kingdom.hasPermission(role, permission, chunk);
                final RolePermissionHolder permissions;
                permissions = Objects.requireNonNullElse(chunk, kingdom);
                permissions.setPermission(role, permission, newValue);
                gui.refresh(slot);
            };

    public static void applyPermissionItemData(GuiItem.Builder builder, String permissionId) {
        final KPermission permission = KPermission.get(permissionId);
        if (permission == null) return;
        applyPermissionItemData(builder, permission);
    }

    public static void applyPermissionItemData(GuiItem.Builder builder, KPermission permission) {
        final RoleManager roleManager = Kingdoms.getPlugin(Kingdoms.class).getRoleManager();
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.SWAP_VALUE_CONSUMER, PERMISSION_SWAP_VALUE_CLICK_ACTION);
        builder.metadata(metadata);
        metadata.put(GuiKeys.PERMISSION, permission);
        builder.metadata(metadata);
        builder.placeholder((gui, item) -> {
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            if (kingdom == null) return false;
            final Role role = roleManager.getRole(gui.getMetadata(GuiKeys.ROLE_ID, String.class), kingdom);
            if (role == null) return false;
            final ClaimedChunk chunk = gui.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
            if (chunk == null) {
                return new PermissionWrapper(permission, kingdom.hasPermission(role, permission));
            }
            return new PermissionWrapper(permission, kingdom.hasPermission(role, permission, chunk));
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
                final BaseGuiItem clicked = gui.getItem(slot);
                if (clicked == null) return;
                final String upgradeId = clicked.getMetadata(GuiKeys.UPGRADE_ID, String.class);
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                final Upgrades<?> upgrades = kingdom.getUpgradeHolder().getUpgrades(upgradeId);
                if (upgrades == null) return;
                kingdomManager.tryLevelUpUpgrade(kingdom, user, upgrades);
                final Integer newLevel = kingdom.getUpgradeLevel(upgrades.getId());
                if (newLevel != null && newLevel >= upgrades.getMaxLevel()) {
                    gui.set(slot, getUpgradeItem(upgrades, newLevel, clicked));
                }
                gui.refresh(slot);
            };

    private static BaseGuiItem getUpgradeItem(Upgrades<?> upgrades, int upgradeLevel, BaseGuiItem clicked) {
        if (upgradeLevel >= upgrades.getMaxLevel()) {
            final BaseGuiItem maxLevelItem = clicked.getMetadata(GuiKeys.MAX_LEVEL_ITEM, BaseGuiItem.class);
            if (maxLevelItem == null) return clicked;
            return maxLevelItem;
        }
        return clicked;
    }

    public static final String MAX_LEVEL_ITEM_PATH = "max-level-item";

    public static void applyUpgradesItemData(GuiItem.Builder builder, String upgradeId, BaseGuiItem maxLevelItem) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.INCREASE_LEVEL_CONSUMER, UPGRADES_INCREASE_LEVEL_ACTION);
        metadata.put(GuiKeys.UPGRADE_ID, upgradeId);
        metadata.put(GuiKeys.MAX_LEVEL_ITEM, maxLevelItem);
        builder.metadata(metadata);
        builder.placeholder((gui, item) -> {
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            if (kingdom == null) return false;
            final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradeId);
            if (upgradeLevel == null) return false;
            final Upgrades<?> upgrades = kingdom.getUpgradeHolder().getUpgrades(upgradeId);
            return new UpgradesWrapper(upgrades, upgradeLevel);
        });
        builder.placeholder((gui, item) -> {
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            if (kingdom == null) return false;
            final Integer upgradeLevel = kingdom.getUpgradeLevel(upgradeId);
            if (upgradeLevel == null) return false;
            return new UpgradeLevelWrapper(kingdom, upgradeId);
        });
    }

    public static void applyRoleItemData(GuiItem.Builder builder, String roleId) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKeys.ROLE_ID, roleId);
        builder.metadata(metadata);
        builder.placeholder((gui, item) -> {
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            if (kingdom == null) return false;
            return kingdom.getRole(roleId);
        });
    }

}