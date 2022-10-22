package io.github.fisher2911.kingdoms.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Gui extends BaseGui {

    // for when a player clicks in their own inventory
    protected final Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler;
    protected final Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers;
    protected final Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler;
    protected final Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler;

    public Gui(
            String name,
            int rows,
            Map<Integer, BaseGuiItem> guiItemsMap,
            Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler,
            Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers,
            Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler,
            Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler
    ) {
        super(name, rows, guiItemsMap);
        this.playerInventoryClickHandler = playerInventoryClickHandler;
        this.defaultEventHandlers = defaultEventHandlers;
        this.closeHandler = closeHandler;
        this.openHandler = openHandler;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        final int slot = event.getSlot();
        final Inventory clickedInventory = event.getClickedInventory();
        final var wrapper = InventoryEventWrapper.wrap(this, event);
        if (event.getView().getBottomInventory().equals(clickedInventory)) {
            if (this.playerInventoryClickHandler != null) {
                this.playerInventoryClickHandler.accept(wrapper);
            }
            return;
        }
        if (!event.getView().getTopInventory().equals(clickedInventory)) {
            return;
        }
        final BaseGuiItem item = this.guiItemsMap.get(slot);
        if (item == null) {
            final var handler = this.defaultEventHandlers.get(InventoryEventType.CLICK);
            if (handler == null) return;
            handler.accept(wrapper);
            return;
        }
        item.handleClick(wrapper);
    }

    @Override
    public void handleDrag(InventoryDragEvent event) {
        final var handler = this.defaultEventHandlers.get(InventoryEventType.DRAG);
        if (handler == null) return;
    }

    @Override
    public void handleClose(InventoryCloseEvent event) {
        final var wrapper = InventoryEventWrapper.wrap(this, event);
        if (this.closeHandler != null) {
            this.closeHandler.accept(wrapper);
            return;
        }
        final var handler = this.defaultEventHandlers.get(InventoryEventType.CLICK);
        if (handler == null) return;
        handler.accept(wrapper);
    }

    @Override
    public void handleOpen(InventoryOpenEvent event) {
        final var wrapper = InventoryEventWrapper.wrap(this, event);
        if (this.openHandler != null) {
            this.openHandler.accept(wrapper);
            return;
        }
        final var handler = this.defaultEventHandlers.get(InventoryEventType.OPEN);
        if (handler == null) return;
        handler.accept(wrapper);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name = "";
        private int rows = 1;
        private final Map<Integer, BaseGuiItem> guiItemsMap = new HashMap<>();
        private Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler;
        private final Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers = new HashMap<>();
        private Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler;
        private Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler;

        private Builder() {}

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder item(int slot, BaseGuiItem item) {
            this.guiItemsMap.put(slot, item);
            return this;
        }

        public Builder items(Map<Integer, BaseGuiItem> items) {
            this.guiItemsMap.putAll(items);
            return this;
        }

        public Builder playerInventoryClickHandler(Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler) {
            this.playerInventoryClickHandler = playerInventoryClickHandler;
            return this;
        }

        public Builder defaultEventHandler(InventoryEventType type, Consumer<InventoryEventWrapper<? extends InventoryEvent>> handler) {
            this.defaultEventHandlers.put(type, handler);
            return this;
        }

        public Builder defaultEventHandlers(Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers) {
            this.defaultEventHandlers.putAll(defaultEventHandlers);
            return this;
        }

        public Builder closeHandler(Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler) {
            this.closeHandler = closeHandler;
            return this;
        }

        public Builder openHandler(Consumer<InventoryEventWrapper<InventoryOpenEvent>> openhandler) {
            this.openHandler = openhandler;
            return this;
        }

        public Gui build() {
            return new Gui(
                    this.name,
                    this.rows,
                    this.guiItemsMap,
                    this.playerInventoryClickHandler,
                    this.defaultEventHandlers,
                    this.closeHandler,
                    this.openHandler
            );
        }
    }
}
