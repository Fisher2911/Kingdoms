package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.serializer.PermissionItemSerializer;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.GuiItemKeys;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuiDisplayItems extends Config {

    private final Map<Integer, BaseGuiItem> permissionItems;

    public GuiDisplayItems(Kingdoms plugin) {
        super(plugin, "guis", "gui-display-items.yml");
        this.permissionItems = new HashMap<>();
    }

    public Map<Integer, BaseGuiItem> getPermissionItems() {
        return permissionItems;
    }

    private static final String PERMISSIONS_SECTION_PATH = "permission-items";
    private static final String DEFAULT_ITEM = "default-item";

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(this.path)
                .build();
        try {
            final var source = loader.load();
            final var permissionItems = source.node(PERMISSIONS_SECTION_PATH);
            final Set<KPermission> usedPermissions = new HashSet<>();
            int lastSlot = 0;
            for (var entry : permissionItems.childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final Integer slot)) continue;
                final BaseGuiItem item = PermissionItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue());
                final KPermission permission = item.getMetadata(GuiItemKeys.PERMISSION, KPermission.class);
                if (permission != null) {
                    usedPermissions.add(permission);
                }
                lastSlot = slot;
                this.permissionItems.put(slot, item);
            }
            final BaseGuiItem defaultItem = PermissionItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, permissionItems.node(DEFAULT_ITEM));
            for (KPermission permission : KPermission.values()) {
                if (usedPermissions.contains(permission)) continue;
                final BaseGuiItem item = defaultItem.copy();
                item.setMetadata(GuiItemKeys.PERMISSION, permission);
                this.permissionItems.put(++lastSlot, item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
