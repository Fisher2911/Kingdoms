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

import io.github.fisher2911.fisherlib.config.serializer.GuiSerializer;
import io.github.fisher2911.fisherlib.config.serializer.ItemSerializers;
import io.github.fisher2911.fisherlib.configurate.ConfigurationNode;
import io.github.fisher2911.fisherlib.configurate.serialize.SerializationException;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.gui.GuiFillerType;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KingdomsGuiSerializer extends GuiSerializer<User, Kingdoms> {

//    private static final Kingdoms PLUGIN = Kingdoms.getPlugin(Kingdoms.class);
//
//    private static final String ID_PATH = "id";
//    private static final String TITLE_PATH = "title";
//    private static final String ROWS_PATH = "rows";
//    private static final String BORDER_ITEMS_PATH = "border";
//    private static final String ITEMS_PATH = "items";
//    private static final boolean CANCEL_CLICKS_PATH = true;
//    private static final String REQUIRED_METADATA_PATH = "required-metadata";
//    private static final String REPEAT_ON_ALL_PAGES_PATH = "repeat-on-all-pages";
//
//    private static final String GUI_FILLERS_PATH = "gui-fillers";

//    private static final Map<GuiFillerType, Function<ConfigurationNode, Function<BaseGui, List<ConditionalItem>>>> GUI_FILLER_LOADERS = Map.of(
//            GuiFillerType.PERMISSIONS, KingdomsGuiSerializer::loadPermissionsFillers,
//            GuiFillerType.UPGRADES, KingdomsGuiSerializer::loadUpgradesFillers,
//            GuiFillerType.ROLES, KingdomsGuiSerializer::loadRolesFillers,
//            GuiFillerType.KINGDOM_MEMBERS, KingdomsGuiSerializer::loadMemberFillers
//    );


    public KingdomsGuiSerializer(ItemSerializers<User, Kingdoms> itemSerializers) {
        super(itemSerializers);
        this.registerGuiFillerLoader(GuiFillerType.PERMISSIONS.toString(), this::loadPermissionsFillers);
        this.registerGuiFillerLoader(GuiFillerType.ROLES.toString(), this::loadRolesFillers);
        this.registerGuiFillerLoader(GuiFillerType.KINGDOM_MEMBERS.toString(), this::loadMemberFillers);
    }

//    public static GuiOpener<User> deserialize(ConfigurationNode source) throws SerializationException {
//        final String id = source.node(ID_PATH).getString();
//        final String title = source.node(TITLE_PATH).getString("Kingdoms");
//        final int rows = source.node(ROWS_PATH).getInt();
//        final boolean cancelClicks = source.node(CANCEL_CLICKS_PATH).getBoolean();
//        final var borderNode = source.node(BORDER_ITEMS_PATH);
//        final List<ConditionalItem> borders = new ArrayList<>();
//        for (var entry : borderNode.childrenMap().entrySet()) {
//            borders.add(KingdomsGuiItemSerializer.INSTANCE.deserialize(ConditionalItem.class, entry.getValue()));
//        }
//        final Set<Integer> repeatOnAllPagesSlots = new HashSet<>();
//        final var itemsNode = source.node(ITEMS_PATH);
//        final Map<Integer, ConditionalItem> items = new HashMap<>();
//        for (var entry : itemsNode.childrenMap().entrySet()) {
//            if (!(entry.getKey() instanceof final Integer slot)) {
//                continue;
//            }
//            final boolean repeatOnAllPages = entry.getValue().node(REPEAT_ON_ALL_PAGES_PATH).getBoolean();
//            if (repeatOnAllPages) repeatOnAllPagesSlots.add(slot);
//            items.put(slot, KingdomsGuiItemSerializer.INSTANCE.deserialize(ConditionalItem.class, entry.getValue()));
//        }
//
//        final var guiFillersNode = source.node(GUI_FILLERS_PATH);
//        final List<Function<BaseGui, List<ConditionalItem>>> guiFillers = new ArrayList<>();
//        for (var entry : guiFillersNode.childrenMap().entrySet()) {
//            if (!(entry.getKey() instanceof final String typeStr)) continue;
//            final GuiFillerType type = EnumUtil.valueOf(GuiFillerType.class, typeStr.toUpperCase());
//            if (type == null) continue;
//            final Function<ConfigurationNode, Function<BaseGui, List<ConditionalItem>>> loader = GUI_FILLER_LOADERS.get(type);
//            if (loader == null) continue;
//            guiFillers.add(loader.apply(entry.getValue()));
//        }
//
//        final List<GuiKeys> requiredMetaData = source.node(REQUIRED_METADATA_PATH)
//                .getList(String.class, new ArrayList<>())
//                .stream()
//                .map(s -> EnumUtil.valueOf(GuiKeys.class, s))
//                .filter(guiKeys -> guiKeys != null)
//                .toList();
//
//        return new GuiOpener(
//                id,
//                Gui.builder(id)
//                        .name(title)
//                        .rows(rows)
//                        .items(items)
//                        .filler(guiFillers)
//                        .border(borders)
//                        .repeatPageSlots(repeatOnAllPagesSlots)
//                        .cancelAllClicks(true),
//                requiredMetaData
//        );
//    }

    private Function<BaseGui, List<ConditionalItem>> loadPermissionsFillers(Kingdoms plugin, ConfigurationNode source) {
        // stupid checked exceptions
        try {
            final List<ConditionalItem> items = new ArrayList<>();
            final ConditionalItem filler = KingdomsGuiItemSerializer.INSTANCE.deserialize(plugin, this.itemSerializers, source);
            for (KPermission permission : KPermission.values()) {
                final ConditionalItem.Builder builder = ConditionalItem.builder(filler);
                KingdomsGuiItemSerializer.INSTANCE.applyPermissionItemData(builder, permission);
                items.add(builder.build());
            }
            return gui -> items;
        } catch (SerializationException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load permission fillers");
        }
    }

    private Function<BaseGui, List<ConditionalItem>> loadRolesFillers(Kingdoms plugin, ConfigurationNode source) {
        // stupid checked exceptions
        try {
            final ConditionalItem filler = KingdomsGuiItemSerializer.INSTANCE.deserialize(plugin, this.itemSerializers, source);
            return gui -> {
                final List<ConditionalItem> items = new ArrayList<>();
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                if (kingdom == null) return items;
                for (Role role : plugin.getRoleManager().getRoles(kingdom)) {
                    final ConditionalItem.Builder builder = ConditionalItem.builder(filler);
                    KingdomsGuiItemSerializer.INSTANCE.applyRoleItemData(builder, role.id());
                    items.add(builder.build());
                }
                return items;
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private Function<BaseGui, List<ConditionalItem>> loadMemberFillers(Kingdoms plugin, ConfigurationNode source) {
        // stupid checked exceptions
        try {
            final ConditionalItem filler = KingdomsGuiItemSerializer.INSTANCE.deserialize(plugin, this.itemSerializers, source);
            return gui -> {
                final List<ConditionalItem> items = new ArrayList<>();
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                if (kingdom == null) return items;
                for (User member : kingdom.getUsers()) {
                    final ConditionalItem.Builder builder = ConditionalItem.builder(filler);
                    KingdomsGuiItemSerializer.INSTANCE.applyMemberItemData(builder, member);
                    items.add(builder.build());
                }
                return items;
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

}
