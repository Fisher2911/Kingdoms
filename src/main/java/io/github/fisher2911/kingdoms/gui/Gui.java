package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
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

    private Gui(
            String id,
            String name,
            int rows,
            Map<Integer, BaseGuiItem> guiItemsMap,
            Map<Object, Object> metadata,
            Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler,
            Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers,
            Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler,
            Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler,
            List<BaseGuiItem> filler,
            List<BaseGuiItem> border,
            int nextPageItemSlot,
            @Nullable GuiItem nextPageItem,
            int previousPageItemSlot,
            @Nullable GuiItem previousPageItem
    ) {
        super(id, name, rows, guiItemsMap, metadata, filler, border, nextPageItemSlot, nextPageItem, previousPageItemSlot, previousPageItem);
        this.playerInventoryClickHandler = playerInventoryClickHandler;
        this.defaultEventHandlers = defaultEventHandlers;
        this.closeHandler = closeHandler;
        this.openHandler = openHandler;
    }

    private Gui(
            String id,
            String name,
            int rows,
            Map<Integer, BaseGuiItem> guiItemsMap,
            Map<Object, Object> metadata,
            Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler,
            Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers,
            Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler,
            Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler,
            List<BaseGuiItem> filler,
            List<BaseGuiItem> border
    ) {
        this(
                id,
                name,
                rows,
                guiItemsMap,
                metadata,
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

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {

        private final String id;
        private String name = "";
        private int rows = 1;
        private final Map<Integer, BaseGuiItem> guiItemsMap = new HashMap<>();
        private final Map<Object, Object> metadata = new HashMap<>();
        private Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler;
        private final Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers = new HashMap<>();
        private Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler;
        private Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler;
        private final List<BaseGuiItem> filler = new ArrayList<>();
        private final List<BaseGuiItem> border = new ArrayList<>();
        private int nextPageItemSlot = -1;
        private @Nullable GuiItem nextPageItem;
        private int previousPageItemSlot = -1;
        private @Nullable GuiItem previousPageItem;

        private Builder(String id) {
            this.id = id;
        }

        private static Builder builder(String id) {
            return new Builder(id);
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

        public Builder metadata(Object key, Object value) {
            this.metadata.put(key, value);
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
            return this.cancelAllClicks(true);
        }

        public Builder cancelAllClicks(boolean value) {
            if (!value) return this;
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

        public Builder filler(List<BaseGuiItem> filler) {
            this.filler.addAll(filler);
            return this;
        }

        public Builder border(List<BaseGuiItem> border) {
            this.border.addAll(border);
            return this;
        }

        public Builder border(BaseGuiItem... border) {
            this.border.addAll(Arrays.asList(border));
            return this;
        }

        public Gui build() {
            return new Gui(
                    this.id,
                    this.name,
                    this.rows,
                    this.guiItemsMap,
                    this.metadata,
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
