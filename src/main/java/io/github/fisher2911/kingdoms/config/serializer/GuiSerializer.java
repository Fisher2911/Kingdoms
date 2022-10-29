package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.Gui;
import io.github.fisher2911.kingdoms.gui.GuiFillerType;
import io.github.fisher2911.kingdoms.gui.GuiItem;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.gui.GuiOpener;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.util.EnumUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class GuiSerializer {

    private static final Kingdoms PLUGIN = Kingdoms.getPlugin(Kingdoms.class);

    private static final String ID_PATH = "id";
    private static final String TITLE_PATH = "title";
    private static final String ROWS_PATH = "rows";
    private static final String BORDER_ITEMS_PATH = "border";
    private static final String ITEMS_PATH = "items";
    private static final boolean CANCEL_CLICKS_PATH = true;
    private static final String REQUIRED_METADATA_PATH = "required-metadata";
    private static final String REPEAT_ON_ALL_PAGES_PATH = "repeat-on-all-pages";

    private static final String GUI_FILLERS_PATH = "gui-fillers";

    private static final Map<GuiFillerType, Function<ConfigurationNode, Function<BaseGui, List<BaseGuiItem>>>> GUI_FILLER_LOADERS = Map.of(
            GuiFillerType.PERMISSIONS, GuiSerializer::loadPermissionsFillers,
            GuiFillerType.UPGRADES, GuiSerializer::loadUpgradesFillers,
            GuiFillerType.ROLES, GuiSerializer::loadRolesFillers
    );

    public static GuiOpener deserialize(ConfigurationNode source) throws SerializationException {
        final String id = source.node(ID_PATH).getString();
        final String title = source.node(TITLE_PATH).getString("Kingdoms");
        final int rows = source.node(ROWS_PATH).getInt();
        final boolean cancelClicks = source.node(CANCEL_CLICKS_PATH).getBoolean();
        final var borderNode = source.node(BORDER_ITEMS_PATH);
        final List<BaseGuiItem> borders = new ArrayList<>();
        for (var entry : borderNode.childrenMap().entrySet()) {
            borders.add(GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue()));
        }
        final Set<Integer> repeatOnAllPagesSlots = new HashSet<>();
        final var itemsNode = source.node(ITEMS_PATH);
        final Map<Integer, BaseGuiItem> items = new HashMap<>();
        for (var entry : itemsNode.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final Integer slot)) {
                continue;
            }
            final boolean repeatOnAllPages = entry.getValue().node(REPEAT_ON_ALL_PAGES_PATH).getBoolean();
            if (repeatOnAllPages) repeatOnAllPagesSlots.add(slot);
            items.put(slot, GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue()));
        }

        final var guiFillersNode = source.node(GUI_FILLERS_PATH);
        final List<Function<BaseGui, List<BaseGuiItem>>> guiFillers = new ArrayList<>();
        for (var entry : guiFillersNode.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final String typeStr)) continue;
            final GuiFillerType type = EnumUtil.valueOf(GuiFillerType.class, typeStr.toUpperCase());
            if (type == null) continue;
            final Function<ConfigurationNode, Function<BaseGui, List<BaseGuiItem>>> loader = GUI_FILLER_LOADERS.get(type);
            if (loader == null) continue;
            guiFillers.add(loader.apply(entry.getValue()));
        }

        final List<GuiKeys> requiredMetaData = source.node(REQUIRED_METADATA_PATH)
                .getList(String.class, new ArrayList<>())
                .stream()
                .map(s -> EnumUtil.valueOf(GuiKeys.class, s))
                .filter(guiKeys -> guiKeys != null)
                .toList();

        return new GuiOpener(
                id,
                Gui.builder(id)
                        .name(title)
                        .rows(rows)
                        .items(items)
                        .filler(guiFillers)
                        .border(borders)
                        .repeatPageSlots(repeatOnAllPagesSlots)
                        .cancelAllClicks(true),
                requiredMetaData
        );
    }

    private static Function<BaseGui, List<BaseGuiItem>> loadPermissionsFillers(final ConfigurationNode source) {
        // stupid checked exceptions
        try {
            final List<BaseGuiItem> items = new ArrayList<>();
            final BaseGuiItem filler = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, source);
            for (KPermission permission : KPermission.values()) {
                final GuiItem.Builder builder = GuiItem.builder(filler);
                GuiItemSerializer.applyPermissionItemData(builder, permission);
                items.add(builder.build());
            }
            return gui -> items;
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Function<BaseGui, List<BaseGuiItem>> loadUpgradesFillers(final ConfigurationNode source) {
        // stupid checked exceptions
        try {
            final BaseGuiItem filler = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, source);
            final BaseGuiItem maxLevelItem = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, source.node(GuiItemSerializer.MAX_LEVEL_ITEM_PATH));
            return gui -> {
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                if (kingdom == null) return new ArrayList<>();
                final List<BaseGuiItem> items = new ArrayList<>();
                for (String id : kingdom.getUpgradeHolder().getUpgradeIdOrder()) {
                    final GuiItem.Builder builder = GuiItem.builder(filler);
                    GuiItemSerializer.applyUpgradesItemData(builder, id, maxLevelItem);
                    items.add(builder.build());
                }
                return items;
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Function<BaseGui, List<BaseGuiItem>> loadRolesFillers(final ConfigurationNode source) {
        // stupid checked exceptions
        try {
            final BaseGuiItem filler = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, source);
            return gui -> {
                final List<BaseGuiItem> items = new ArrayList<>();
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                if (kingdom == null) return items;
                for (Role role : PLUGIN.getRoleManager().getRoles(kingdom)) {
                    System.out.println("role = " + role);
                    final GuiItem.Builder builder = GuiItem.builder(filler);
                    GuiItemSerializer.applyRoleItemData(builder, role.id());
                    items.add(builder.build());
                }
                return items;
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

}
