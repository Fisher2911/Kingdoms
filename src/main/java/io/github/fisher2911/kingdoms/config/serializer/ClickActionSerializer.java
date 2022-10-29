package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.ClickAction;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
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
    }

    private static final String CLICK_TYPES_PATH = "click-types";
    private static final String COMMAND_PATH = "command";
    private static final String MENU_PATH = "menu";
    private static final String RESET_DELAY_PATH = "reset-delay";

    public static List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> deserializeAll(ConfigurationNode source) throws SerializationException {
        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> actions = new ArrayList<>();
        for (var entry : source.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final String action)) continue;
            final var consumer = deserialize(entry.getValue(), action.toUpperCase(Locale.ROOT));
            if (consumer == null) continue;
            actions.add(consumer);
        }
        return actions;
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> deserialize(
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
            if (type == CommandSenderType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            } else {
                Bukkit.getServer().dispatchCommand(event.getWhoClicked(), command);
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
            final BaseGuiItem item = gui.getItem(event.getSlot());
            if (item != null) {
                final List<String> keys = item.getMetadata(GuiKeys.SEND_DATA_KEYS, List.class);
                if (keys != null) {
                    for (String key : keys) {
                        final GuiKeys guiKey = EnumUtil.valueOf(GuiKeys.class, key);
                        if (guiKey == null) continue;
                        metadata.put(guiKey, item.getMetadata(guiKey));
                    }
                }
            }
            guiManager.open(menu, wrapper.gui().getMetadata(GuiKeys.USER, User.class), metadata);
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadDelete(ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getItem(clicked);
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
            final BaseGuiItem item = wrapper.gui().getItem(clicked);
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
            final BaseGuiItem item = wrapper.gui().getItem(clicked);
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
            event.getWhoClicked().sendMessage("Closing menu");
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
            final Map<Integer, BaseGuiItem> items = new HashMap<>();
            final int duration = source.node(RESET_DELAY_PATH).getInt(-1);
            for (var entry : source.node(ITEMS_PATH).childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final Integer slot)) continue;
                final BaseGuiItem item = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue());
                if (item == null) continue;
                items.put(slot, item);
            }
            final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final Map<Integer, BaseGuiItem> original = new HashMap<>();
                for (var entry : items.entrySet()) {
                    final int slot = entry.getKey();
                    final BaseGuiItem item = wrapper.gui().getItem(slot);
                    if (duration != -1) original.put(slot, item);
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
            final BaseGuiItem item = GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, source.node(ITEM_PATH));
            if (item == null) throw new SerializationException("Item cannot be null");
            final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final BaseGuiItem original = wrapper.gui().getItem(event.getSlot());
                final int clicked = event.getSlot();
                wrapper.gui().set(clicked, item);
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
            final BaseGuiItem item = wrapper.gui().getItem(clicked);
            if (item == null) return;
            final BaseGui gui = wrapper.gui();
            final User user = gui.getMetadata(GuiKeys.USER, User.class);
            if (user == null) return;
            plugin.getKingdomManager().tryDisband(user, true);
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
                final BaseGuiItem item = wrapper.gui().getItem(clicked);
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
            final String previousGui = wrapper.gui().getMetadata(GuiKeys.PREVIOUS_MENU_ID, String.class);
            System.out.println("Going back to: " + previousGui);
            if (previousGui == null) return;
            plugin.getGuiManager().open(previousGui, userManager.wrap(event.getWhoClicked()));
        };

    }

}
