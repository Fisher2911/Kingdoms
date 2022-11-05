package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.ClickAction;
import io.github.fisher2911.kingdoms.gui.ConditionalItem;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import io.github.fisher2911.kingdoms.util.EnumUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClickActionSerializer {

    private static final Map<ClickAction, BiFunction<ConfigurationNode, Set<ClickType>, Consumer<InventoryEventWrapper<InventoryClickEvent>>>> LOADERS =
            new HashMap<>();

    static {
        LOADERS.put(ClickAction.NEXT_PAGE, ClickActionSerializer::loadNextPage);
        LOADERS.put(ClickAction.PREVIOUS_PAGE, ClickActionSerializer::loadPreviousPage);
        LOADERS.put(ClickAction.PLAYER_COMMAND, ClickActionSerializer::loadPlayerCommand);
        LOADERS.put(ClickAction.CONSOLE_COMMAND, ClickActionSerializer::loadConsoleCommand);
        LOADERS.put(ClickAction.OPEN_MENU, ClickActionSerializer::loadOpenMenu);
        LOADERS.put(ClickAction.DELETE, ClickActionSerializer::loadDelete);
        LOADERS.put(ClickAction.INCREASE_LEVEL, ClickActionSerializer::loadIncreaseLevel);
        LOADERS.put(ClickAction.SET_ITEMS, ClickActionSerializer::loadSetItems);
        LOADERS.put(ClickAction.SET_ITEM, ClickActionSerializer::loadSetItem);
        LOADERS.put(ClickAction.SWAP_VALUE, ClickActionSerializer::loadSwapValue);
        LOADERS.put(ClickAction.CLOSE_MENU, ClickActionSerializer::loadCloseMenu);
        LOADERS.put(ClickAction.DELETE_KINGDOM, ClickActionSerializer::loadDeleteKingdom);
        LOADERS.put(ClickAction.SEND_DATA, ClickActionSerializer::loadSendData);
        LOADERS.put(ClickAction.PREVIOUS_GUI, ClickActionSerializer::loadPreviousGui);
        LOADERS.put(ClickAction.SET_MEMBER_ROLE, ClickActionSerializer::loadSetMemberRole);
        LOADERS.put(ClickAction.KICK_MEMBER, ClickActionSerializer::loadKickMember);
    }

    private static final String CLICK_TYPES_PATH = "click-types";
    private static final String COMMAND_PATH = "command";
    private static final String MENU_PATH = "menu";
    private static final String RESET_DELAY_PATH = "duration";

    public static List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> deserializeAll(ConfigurationNode source) throws SerializationException {
        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> actions = new ArrayList<>();
        for (var actionsEntry : source.childrenMap().entrySet()) {
            final var node = actionsEntry.getValue();
            for (var entry : node.childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final String action)) continue;
                final var consumer = deserialize(node.node(action), action.toUpperCase(Locale.ROOT));
                if (consumer == null) continue;
                actions.add(consumer);
            }
        }
        return actions;
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> deserialize(
            ConfigurationNode source,
            String actionType
    ) throws SerializationException {
        final Set<ClickType> clickTypes = source.node(CLICK_TYPES_PATH).getList(String.class, new ArrayList<>())
                .stream()
                .map(s -> EnumUtil.valueOf(ClickType.class, s))
                .filter(clickType -> clickType != null)
                .collect(Collectors.toSet());
        final ClickAction action = EnumUtil.valueOf(ClickAction.class, actionType);
        if (action == null) {
            throw new SerializationException("Invalid click action: " + actionType);
        }
        final var loader = LOADERS.get(action);
        if (loader == null) {
            throw new SerializationException("No loader for click action: " + actionType);
        }
        return loader.apply(source, clickTypes);
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadNextPage(ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            wrapper.gui().goToNextPage();
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadPreviousPage(ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            wrapper.gui().goToPreviousPage();
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadCommand(ConfigurationNode source, Set<ClickType> clickTypes, CommandSenderType type) {
        if (type == CommandSenderType.ANY) throw new IllegalArgumentException("Command sender type cannot be ANY");
        final String command = source.node(COMMAND_PATH).getString();
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final String parsed = PlaceholderBuilder.apply(command, wrapper.gui());
            if (type == CommandSenderType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsed);
            } else {
                Bukkit.getServer().dispatchCommand(event.getWhoClicked(), parsed);
            }
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadPlayerCommand(ConfigurationNode source, Set<ClickType> clickTypes) {
        return loadCommand(source, clickTypes, CommandSenderType.PLAYER);
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadConsoleCommand(ConfigurationNode source, Set<ClickType> clickTypes) {
        return loadCommand(source, clickTypes, CommandSenderType.CONSOLE);
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadOpenMenu(ConfigurationNode source, Set<ClickType> clickTypes) {
        final String menu = source.node(MENU_PATH).getString();
        if (menu == null) throw new IllegalArgumentException("Menu cannot be null");
        final GuiManager guiManager = Kingdoms.getPlugin(Kingdoms.class).getGuiManager();
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final BaseGui gui = wrapper.gui();
            final Map<Object, Object> metadata = new HashMap<>();
            List<String> previousIds = gui.getMetadata(GuiKeys.PREVIOUS_MENU_ID, List.class);
            if (previousIds == null) previousIds = new ArrayList<>();
            if (previousIds.contains(gui.getId())) {
                previousIds.remove(gui.getId());
            } else {
                previousIds.add(gui.getId());
            }
            metadata.put(GuiKeys.PREVIOUS_MENU_ID, previousIds);
            final BaseGuiItem item = gui.getBaseGuiItem(event.getSlot());
            if (item != null) {
                final List<String> keys = item.getMetadata(GuiKeys.SEND_DATA_KEYS, List.class);
                final ConditionalItem conditionalItem = gui.getItem(event.getSlot());
                if (keys != null) {
                    for (String key : keys) {
                        final GuiKeys guiKey = EnumUtil.valueOf(GuiKeys.class, key);
                        if (guiKey == null) continue;
                        Object toSend = item.getMetadata(guiKey);
                        if (toSend == null) {
                            if (conditionalItem == null) continue;
                            toSend = conditionalItem.getMetadata(guiKey);
                            if (toSend == null) continue;
                        }
                        metadata.put(guiKey, toSend);
                    }
                }
            }
            guiManager.open(menu, wrapper.gui().getMetadata(GuiKeys.USER, User.class), metadata, metadata.keySet());
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadDelete(ConfigurationNode source, Set<ClickType> clickTypes) {
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

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadIncreaseLevel(ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
            if (item == null) return;
            final Consumer<InventoryEventWrapper<InventoryClickEvent>> increaser = item.getMetadata(GuiKeys.INCREASE_LEVEL_CONSUMER, Consumer.class);
            if (increaser == null) return;
            increaser.accept(wrapper);
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSwapValue(ConfigurationNode source, Set<ClickType> clickTypes) {
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

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadCloseMenu(ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            event.getWhoClicked().closeInventory();
        };
    }

    private static final String ITEMS_PATH = "items";

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSetItems(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        // stupid checked exceptions not working with lambdas
        try {
            final Map<Integer, ConditionalItem> items = new HashMap<>();
            final int duration = source.node(RESET_DELAY_PATH).getInt(-1);
            for (var entry : source.node(ITEMS_PATH).childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final Integer slot)) continue;
                final ConditionalItem item = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue());
                if (item == null) continue;
                items.put(slot, item);
            }
            final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final Map<Integer, ConditionalItem> original = new HashMap<>();
                for (var entry : items.entrySet()) {
                    final int slot = entry.getKey();
                    final ConditionalItem conditionalItem = wrapper.gui().getItem(slot);
                    final BaseGuiItem item = wrapper.gui().getBaseGuiItem(slot);
                    if (duration != -1) original.put(slot, conditionalItem);
                    wrapper.gui().set(entry.getKey(), entry.getValue());
                }
                if (duration == -1) return;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (var entry : original.entrySet()) {
                        wrapper.gui().set(entry.getKey(), entry.getValue());
                    }
                }, duration);
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String ITEM_PATH = "item";

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSetItem(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        // stupid checked exceptions not working with lambdas
        try {
            final int duration = source.node(RESET_DELAY_PATH).getInt(-1);
            final ConditionalItem item = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, source/*.node(ITEM_PATH)*/);
            if (item == null) throw new SerializationException("Item cannot be null");
            final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final ConditionalItem original = wrapper.gui().getItem(event.getSlot());
                final int clicked = event.getSlot();
                final BaseGuiItem originalItem = wrapper.gui().getBaseGuiItem(clicked);
                final ConditionalItem.Builder builder = originalItem == null ? null : ConditionalItem.builder(item);
                if (originalItem != null) {
                    builder.metadata(originalItem.getMetadata().get(), true);
                }
                wrapper.gui().set(clicked, builder.build());
                if (duration == -1) return;
                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> wrapper.gui().set(clicked, original),
                        duration
                );
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadDeleteKingdom(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
            if (item == null) return;
            final BaseGui gui = wrapper.gui();
            final User user = gui.getMetadata(GuiKeys.USER, User.class);
            if (user == null) return;
            plugin.getKingdomManager().tryDisband(user, true, true);
        };
    }

    private static final String DATA_PATH = "data";

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSendData(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        // stupid checked exceptions not working with lambdas
        try {
            final List<String> keys = source.node(DATA_PATH).getList(String.class, new ArrayList<>())
                    .stream()
                    .map(String::toUpperCase).
                    collect(Collectors.toList());
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final int clicked = event.getSlot();
                final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
                if (item == null) return;
                item.setMetadata(GuiKeys.SEND_DATA_KEYS, keys);
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadPreviousGui(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
        final UserManager userManager = plugin.getUserManager();
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final List<String> previousGuis = wrapper.gui().getMetadata(GuiKeys.PREVIOUS_MENU_ID, List.class);
            if (previousGuis == null || previousGuis.isEmpty()) return;
            final String previousGui = previousGuis.remove(previousGuis.size() - 1);
            if (previousGui == null) return;
            final User user = userManager.forceGet(event.getWhoClicked().getUniqueId());
            if (user == null) return;
            plugin.getGuiManager().open(previousGui, user, Map.of(GuiKeys.PREVIOUS_MENU_ID, previousGuis), Set.of());
        };

    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSetMemberRole(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final BaseGui gui = wrapper.gui();
            final BaseGuiItem clickedItem = gui.getBaseGuiItem(event.getSlot());
            if (clickedItem == null) return;
            final User user = gui.getMetadata(GuiKeys.USER, User.class);
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            final User kingdomMember = gui.getMetadata(GuiKeys.KINGDOM_MEMBER, User.class);
            if (user == null || kingdom == null || kingdomMember == null) return;
            final String roleId = clickedItem.getMetadata(GuiKeys.ROLE_ID, String.class);
            plugin.getKingdomManager().trySetRole(kingdom, user, kingdomMember, roleId);
            gui.resetAndRefresh();
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadKickMember(
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final BaseGui gui = wrapper.gui();
            final BaseGuiItem clickedItem = gui.getBaseGuiItem(event.getSlot());
            if (clickedItem == null) return;
            final User user = gui.getMetadata(GuiKeys.USER, User.class);
            final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
            final User kingdomMember = clickedItem.getMetadata(GuiKeys.KINGDOM_MEMBER, User.class);
            if (user == null || kingdom == null || kingdomMember == null) return;
            plugin.getKingdomManager().tryKick(kingdom, user, kingdomMember);
        };
    }

}
