package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.ClickAction;
import io.github.fisher2911.kingdoms.gui.GuiItem;
import io.github.fisher2911.kingdoms.gui.GuiItemKeys;
import io.github.fisher2911.kingdoms.gui.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.gui.type.PermissionGui;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class PermissionItemSerializer implements TypeSerializer<BaseGuiItem> {

    public static final PermissionItemSerializer INSTANCE = new PermissionItemSerializer();

    private PermissionItemSerializer() {
    }

    private static final Map<ClickAction, Function<Collection<ClickType>, Consumer<InventoryEventWrapper<InventoryClickEvent>>>> LOADERS = new HashMap<>();

    static {
        LOADERS.put(ClickAction.NEXT_PAGE, GuiItem::nextPageWrapper);
        LOADERS.put(ClickAction.PREVIOUS_PAGE, GuiItem::previousPageWrapper);
        LOADERS.put(ClickAction.SWAP_VALUE, PermissionGui::swapValueItem);
        LOADERS.put(ClickAction.NONE, null);
    }

    private static final String ITEM_PATH = "item";
    private static final String PERMISSION_PATH = "permission";
    private static final String ACTIONS_PATH = "actions";
    private static final String CLICK_TYPES_PATH = "click-types";

    @Override
    public BaseGuiItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final ConfigurationNode itemNode = node.node(ITEM_PATH);
        final ConfigurationNode permissionNode = node.node(PERMISSION_PATH);
        final ConfigurationNode actionsNode = node.node(ACTIONS_PATH);
        final KPermission permission = KPermission.get(permissionNode.getString(""));
        final ItemBuilder itemBuilder = ItemSerializer.INSTANCE.deserialize(ItemBuilder.class, itemNode);
        final Map<ClickType, Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers = new HashMap<>();
        for (var entry : actionsNode.childrenMap().entrySet()) {
            final List<ClickType> clickTypes = entry.getValue().node(CLICK_TYPES_PATH).getList(ClickType.class, new ArrayList<>());
            final ClickAction clickActionType = ClickAction.valueOf(entry.getKey().toString().toUpperCase(Locale.ROOT));
            final var consumer = LOADERS.get(clickActionType);
            if (consumer == null) continue;
            for (ClickType clickType : clickTypes) {
                clickHandlers.put(clickType, consumer.apply(List.of(clickType)));
            }
        }
        final GuiItem.Builder builder = GuiItem.builder(itemBuilder).
                clickHandler(event -> {
                    event.event().setCancelled(true);
                    final Consumer<InventoryEventWrapper<InventoryClickEvent>> consumer = clickHandlers.get(event.event().getClick());
                    if (consumer == null) return;
                    consumer.accept(event);
                });
        if (permission != null) {
            builder.metadata(Map.of(GuiItemKeys.PERMISSION, permission));
        }
        return builder.build();
    }

    @Override
    public void serialize(Type type, @Nullable BaseGuiItem obj, ConfigurationNode node) throws SerializationException {

    }

}
