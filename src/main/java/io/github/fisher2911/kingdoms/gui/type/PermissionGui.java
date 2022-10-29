package io.github.fisher2911.kingdoms.gui.type;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.serializer.GuiSerializer;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.GuiOpener;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class PermissionGui {

    private final BaseGui gui;

    private PermissionGui(BaseGui gui) {
        this.gui = gui;
    }

    public static GuiOpener create(User user, Kingdoms plugin, Role role, Kingdom kingdom, ClaimedChunk chunk) {
        final Path path = plugin.getDataFolder().toPath().resolve("guis").resolve("permissions.yml");
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(path).build();
        try {
            return GuiSerializer.deserialize(
                    loader.load()
            );
//            final BaseGui gui = GuiSerializer.deserialize(
//                    loader.load()
//            ).metadata(GuiKeys.KINGDOM, kingdom)
//                    .metadata(GuiKeys.ROLE, role)
//                    .metadata(GuiKeys.CHUNK, chunk).
//                    metadata(GuiKeys.USER, user).
//                    build();
//            return gui;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        final GuiDisplayItems displayItems = plugin.getGuiDisplayItems();
//        final Map<Integer, BaseGuiItem> items = new HashMap<>();
//        int index = 0;
//        for (var entry : displayItems.getPermissionItems().entrySet()) {
//            final int slot = entry.getKey();
//            final BaseGuiItem guiItem = entry.getValue();
//            final KPermission permission = guiItem.getMetadata(GuiItemKeys.PERMISSION, KPermission.class);
//            if (permission == null) {
//                items.put(slot, guiItem);
//                continue;
//            }
//            if (chunk == null && !permission.hasContext(PermissionContext.KINGDOM)) continue;
//            if (chunk != null && !permission.hasContext(PermissionContext.CLAIM)) continue;
//
//            final List<Supplier<Object>> placeholders = new ArrayList<>();
//            if (chunk == null) {
//                placeholders.add(() -> new PermissionWrapper(permission, kingdom.hasPermission(role, permission)));
//            } else {
//                placeholders.add(() -> new PermissionWrapper(permission, kingdom.hasPermission(role, permission, chunk)));
//            }
//
//            guiItem.setMetadata(GuiItemKeys.PERMISSION, permission);
//            guiItem.setMetadata(GuiItemKeys.KINGDOM, kingdom);
//            guiItem.setMetadata(GuiItemKeys.ROLE, role);
//            guiItem.setMetadata(GuiItemKeys.CHUNK, chunk);
//
//            items.put(index, guiItem.withPlaceholders(placeholders));
//            index++;
//        }
//        return new PermissionGui(
//                Gui.builder("permissions").
//                        name("<gray>Permissions").
//                        items(items).
//                        rows(items.size() / 9 + 1).
//                        cancelAllClicks().
//                        build()
//        );
    }
//
//    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> swapValueItem(Collection<ClickType> clickTypes) {
//        return wrapper -> {
//            if (!clickTypes.contains(wrapper.event().getClick())) return;
//            final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
//            final UserManager userManager = plugin.getUserManager();
//            final InventoryClickEvent event = wrapper.event();
//            final BaseGui gui = wrapper.gui();
//            final int slot = event.getSlot();
//            final BaseGuiItem clicked = gui.getItem(slot);
//            if (clicked == null) return;
//            final KPermission permission = clicked.getMetadata(GuiKeys.PERMISSION, KPermission.class);
//            if (permission == null) return;
//            final Kingdom kingdom = clicked.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
//            final Role role = clicked.getMetadata(GuiKeys.ROLE, Role.class);
//            final ClaimedChunk chunk = clicked.getMetadata(GuiKeys.CHUNK, ClaimedChunk.class);
//            event.setCancelled(true);
//            final User user = userManager.wrap(event.getWhoClicked());
//            if ((chunk != null && !kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS, chunk)) &&
//                    (!kingdom.hasPermission(user, KPermission.EDIT_LOWER_ROLES_PERMISSIONS))) {
//                MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
//                return;
//            }
//
//            if (chunk != null && !kingdom.hasPermission(user, permission, chunk) || (chunk == null && !kingdom.hasPermission(user, permission))) {
//                MessageHandler.sendMessage(user, Message.CANNOT_EDIT_KINGDOM_PERMISSION);
//                return;
//            }
//            final boolean newValue = chunk == null ? !kingdom.hasPermission(role, permission) : !kingdom.hasPermission(role, permission, chunk);
//            final RolePermissionHolder permissions;
//            permissions = Objects.requireNonNullElse(chunk, kingdom);
//            permissions.setPermission(role, permission, newValue);
//            gui.refresh(slot);
//        };
//    }

    public void open(HumanEntity human) {
        this.gui.open(human);
    }
}
