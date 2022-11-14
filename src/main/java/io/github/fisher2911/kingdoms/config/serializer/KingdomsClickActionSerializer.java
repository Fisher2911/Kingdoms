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

import io.github.fisher2911.fisherlib.config.serializer.ClickActionSerializer;
import io.github.fisher2911.fisherlib.config.serializer.ItemSerializers;
import io.github.fisher2911.fisherlib.configurate.ConfigurationNode;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.BaseGuiItem;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.gui.ClickAction;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;
import java.util.function.Consumer;

public class KingdomsClickActionSerializer extends ClickActionSerializer<User, Kingdoms> {

//    private static final Map<ClickAction, BiFunction<ConfigurationNode, Set<ClickType>, Consumer<InventoryEventWrapper<InventoryClickEvent>>>> LOADERS =
//            new HashMap<>();

    public static final KingdomsClickActionSerializer INSTANCE = new KingdomsClickActionSerializer();

//    static {
//        LOADERS.put(ClickAction.DELETE, KingdomsClickActionSerializer::loadDelete);
//        LOADERS.put(ClickAction.SWAP_VALUE, KingdomsClickActionSerializer::loadSwapValue);
//        LOADERS.put(ClickAction.DELETE_KINGDOM, KingdomsClickActionSerializer::loadDeleteKingdom);
//        LOADERS.put(ClickAction.SET_MEMBER_ROLE, KingdomsClickActionSerializer::loadSetMemberRole);
//        LOADERS.put(ClickAction.KICK_MEMBER, KingdomsClickActionSerializer::loadKickMember);
//    }

    private KingdomsClickActionSerializer() {
        this.registerLoader(ClickAction.DELETE.toString(), this::loadDelete);
        this.registerLoader(ClickAction.SWAP_VALUE.toString(), this::loadSwapValue);
        this.registerLoader(ClickAction.DELETE_KINGDOM.toString(), this::loadDeleteKingdom);
        this.registerLoader(ClickAction.SET_MEMBER_ROLE.toString(), this::loadSetMemberRole);
        this.registerLoader(ClickAction.KICK_MEMBER.toString(), this::loadKickMember);
    }

//    public static List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> deserializeAll(ConfigurationNode source) throws SerializationException {
//        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> actions = new ArrayList<>();
//        for (var actionsEntry : source.childrenMap().entrySet()) {
//            final var node = actionsEntry.getValue();
//            for (var entry : node.childrenMap().entrySet()) {
//                if (!(entry.getKey() instanceof final String action)) continue;
//                final var consumer = deserialize(node.node(action), action.toUpperCase(Locale.ROOT));
//                if (consumer == null) continue;
//                actions.add(consumer);
//            }
//        }
//        return actions;
//    }

//    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> deserialize(
//            ConfigurationNode source,
//            String actionType
//    ) throws SerializationException {
//        final Set<ClickType> clickTypes = source.node(CLICK_TYPES_PATH).getList(String.class, new ArrayList<>())
//                .stream()
//                .map(s -> EnumUtil.valueOf(ClickType.class, s))
//                .filter(clickType -> clickType != null)
//                .collect(Collectors.toSet());
//        final ClickAction action = EnumUtil.valueOf(ClickAction.class, actionType);
//        if (action == null) {
//            throw new SerializationException("Invalid click action: " + actionType);
//        }
//        final var loader = LOADERS.get(action);
//        if (loader == null) {
//            throw new SerializationException("No loader for click action: " + actionType);
//        }
//        return loader.apply(source, clickTypes);
//    }

    private Consumer<InventoryEventWrapper<InventoryClickEvent>> loadDelete(Kingdoms plugin, ItemSerializers<User, Kingdoms> serializers, ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
            if (item == null) return;
            final Consumer<InventoryEventWrapper<InventoryClickEvent>> deleter = item.getMetadata(GuiKeys.DELETE_CONSUMER, Consumer.class);
            if (deleter == null) return;
            deleter.accept(wrapper);
        };
    }

    private Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSwapValue(
            Kingdoms plugin,
            ItemSerializers<User, Kingdoms> serializers,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
            if (item == null) return;
            final Consumer<InventoryEventWrapper<InventoryClickEvent>> swapper = item.getMetadata(GuiKeys.SWAP_VALUE_CONSUMER, Consumer.class);
            if (swapper == null) return;
            swapper.accept(wrapper);
        };
    }

    private Consumer<InventoryEventWrapper<InventoryClickEvent>> loadDeleteKingdom(
            Kingdoms plugin,
            ItemSerializers<User, Kingdoms> serializers,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
            if (item == null) return;
            final BaseGui gui = wrapper.gui();
            final User user = gui.getMetadata(GuiKey.USER, User.class);
            if (user == null) return;
            plugin.getKingdomManager().tryDisband(user, true, true);
        };
    }

    private Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSetMemberRole(
            Kingdoms plugin,
            ItemSerializers<User, Kingdoms> serializers,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final BaseGui gui = wrapper.gui();
            final BaseGuiItem clickedItem = gui.getBaseGuiItem(event.getSlot());
            if (clickedItem == null) return;
            final User user = gui.getMetadata(GuiKey.USER, User.class);
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            final User kingdomMember = gui.getMetadata(GuiKeys.KINGDOM_MEMBER, User.class);
            if (user == null || kingdom == null || kingdomMember == null) return;
            final String roleId = clickedItem.getMetadata(GuiKeys.ROLE_ID, String.class);
            plugin.getKingdomManager().trySetRole(kingdom, user, kingdomMember, roleId);
            gui.resetAndRefresh();
        };
    }

    private Consumer<InventoryEventWrapper<InventoryClickEvent>> loadKickMember(
            Kingdoms plugin,
            ItemSerializers<User, Kingdoms> serializers,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final BaseGui gui = wrapper.gui();
            final BaseGuiItem clickedItem = gui.getBaseGuiItem(event.getSlot());
            if (clickedItem == null) return;
            final User user = gui.getMetadata(GuiKey.USER, User.class);
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            final User kingdomMember = clickedItem.getMetadata(GuiKeys.KINGDOM_MEMBER, User.class);
            if (user == null || kingdom == null || kingdomMember == null) return;
            plugin.getKingdomManager().tryKick(kingdom, user, kingdomMember);
        };
    }

}
