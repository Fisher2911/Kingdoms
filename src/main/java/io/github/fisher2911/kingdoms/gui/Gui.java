package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
            Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler,
            @Nullable ItemBuilder filler,
            List<ItemBuilder> border,
            int nextPageItemSlot,
            @Nullable GuiItem nextPageItem,
            int previousPageItemSlot,
            @Nullable GuiItem previousPageItem
    ) {
        super(name, rows, guiItemsMap, filler, border, nextPageItemSlot, nextPageItem, previousPageItemSlot, previousPageItem);
        this.playerInventoryClickHandler = playerInventoryClickHandler;
        this.defaultEventHandlers = defaultEventHandlers;
        this.closeHandler = closeHandler;
        this.openHandler = openHandler;
    }

    public Gui(
            String name,
            int rows,
            Map<Integer, BaseGuiItem> guiItemsMap,
            Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler,
            Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers,
            Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler,
            Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler,
            @Nullable ItemBuilder filler,
            List<ItemBuilder> border
    ) {
        this(
                name,
                rows,
                guiItemsMap,
                playerInventoryClickHandler,
                defaultEventHandlers,
                closeHandler,
                openHandler,
                filler,
                border,
                -1,
                null,
                -1,
                null
        );
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        final int slot = event.getSlot();
        final Inventory clickedInventory = event.getClickedInventory();
        final var wrapper = InventoryEventWrapper.wrap(this, event);
        if (event.getView().getBottomInventory().equals(clickedInventory)) {
            Bukkit.broadcastMessage("Clicked bottom inventory in GUI class");
            if (this.playerInventoryClickHandler != null) {
                this.playerInventoryClickHandler.accept(wrapper);
            }
            Bukkit.broadcastMessage("Is handler null? " + (this.playerInventoryClickHandler == null));
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
        handler.accept(InventoryEventWrapper.wrap(this, event));
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
        private @Nullable ItemBuilder filler;
        private final List<ItemBuilder> border = new ArrayList<>();

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

        public Builder cancelClicks() {
            this.defaultEventHandlers.put(InventoryEventType.CLICK, InventoryEventWrapper::cancel);
            return this;
        }

        public Builder cancelDrags() {
            this.defaultEventHandlers.put(InventoryEventType.DRAG, InventoryEventWrapper::cancel);
            return this;
        }

        public Builder cancelPlayerClicks() {
            this.playerInventoryClickHandler = InventoryEventWrapper::cancel;
            return this;
        }

        public Builder cancelAllClicks() {
            this.cancelClicks();
            this.cancelDrags();
            this.cancelPlayerClicks();
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

        public Builder filler(@Nullable ItemBuilder filler) {
            this.filler = filler;
            return this;
        }

        public Builder border(List<ItemBuilder> border) {
            this.border.addAll(border);
            return this;
        }

        public Builder border(ItemBuilder... border) {
            this.border.addAll(Arrays.asList(border));
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
                    this.openHandler,
                    this.filler,
                    this.border
            );
        }
    }
}
