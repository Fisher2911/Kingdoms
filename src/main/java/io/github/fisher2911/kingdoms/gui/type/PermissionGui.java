package io.github.fisher2911.kingdoms.gui.type;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.GuiDisplayItems;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.Gui;
import io.github.fisher2911.kingdoms.gui.GuiItemKeys;
import io.github.fisher2911.kingdoms.gui.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContext;
import io.github.fisher2911.kingdoms.kingdom.permission.RolePermissionHolder;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PermissionGui {

    private final BaseGui gui;

    private PermissionGui(BaseGui gui) {
        this.gui = gui;
    }

    public static PermissionGui create(final Kingdoms plugin, Role role, Kingdom kingdom, ClaimedChunk chunk) {
        final GuiDisplayItems displayItems = plugin.getGuiDisplayItems();
        final Map<Integer, BaseGuiItem> items = new HashMap<>();
        int index = 0;
        for (var entry : displayItems.getPermissionItems().entrySet()) {
            final int slot = entry.getKey();
            final BaseGuiItem guiItem = entry.getValue();
            final KPermission permission = guiItem.getMetadata(GuiItemKeys.PERMISSION, KPermission.class);
            if (permission == null) {
                items.put(slot, guiItem);
                continue;
            }
            if (chunk == null && !permission.hasContext(PermissionContext.KINGDOM)) continue;
            if (chunk != null && !permission.hasContext(PermissionContext.CLAIM)) continue;

            final List<Supplier<Object>> placeholders = new ArrayList<>();
            if (chunk == null) {
                placeholders.add(() -> new PermissionWrapper(permission, kingdom.hasPermission(role, permission)));
            } else {
                placeholders.add(() -> new PermissionWrapper(permission, kingdom.hasPermission(role, permission, chunk)));
            }

            guiItem.setMetadata(GuiItemKeys.PERMISSION, permission);
            guiItem.setMetadata(GuiItemKeys.KINGDOM, kingdom);
            guiItem.setMetadata(GuiItemKeys.ROLE, role);
            guiItem.setMetadata(GuiItemKeys.CHUNK, chunk);

            items.put(index, guiItem.withPlaceholders(placeholders));
            index++;
        }
        return new PermissionGui(
                Gui.builder().
                        name("<gray>Permissions").
                        items(items).
                        rows(items.size() / 9 + 1).
                        cancelAllClicks().
                        build()
        );
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> swapValueItem(Collection<ClickType> clickTypes) {
        return wrapper -> {
            if (!clickTypes.contains(wrapper.event().getClick())) return;
            final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
            final UserManager userManager = plugin.getUserManager();
            final InventoryClickEvent event = wrapper.event();
            final BaseGui gui = wrapper.gui();
            final int slot = event.getSlot();
            final BaseGuiItem clicked = gui.getItem(slot);
            if (clicked == null) return;
            final KPermission permission = clicked.getMetadata(GuiItemKeys.PERMISSION, KPermission.class);
            if (permission == null) return;
            final Kingdom kingdom = clicked.getMetadata(GuiItemKeys.KINGDOM, Kingdom.class);
            final Role role = clicked.getMetadata(GuiItemKeys.ROLE, Role.class);
            final ClaimedChunk chunk = clicked.getMetadata(GuiItemKeys.CHUNK, ClaimedChunk.class);
            event.setCancelled(true);
            final User user = userManager.wrap(event.getWhoClicked());
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
    }

    public void open(HumanEntity human) {
        this.gui.open(human);
    }
}
