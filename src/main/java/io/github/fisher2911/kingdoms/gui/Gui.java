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

package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.util.Metadata;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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
            Map<Integer, ConditionalItem> guiItemsMap,
            Set<Integer> repeatPageSlots,
            Metadata metadata,
            Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler,
            Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers,
            Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler,
            Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler,
            List<Function<BaseGui, List<ConditionalItem>>> filler,
            List<ConditionalItem> border
    ) {
        super(id, name, rows, guiItemsMap, repeatPageSlots, metadata, filler, border);
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
        final BaseGuiItem item = this.getBaseGuiItem(this.getItemPageSlot(slot)/*, true*/);
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
        private final Map<Integer, ConditionalItem> guiItemsMap = new HashMap<>();
        private final Set<Integer> repeatPageSlots = new HashSet<>();
        private final Metadata metadata = new Metadata(new HashMap<>());
        private Consumer<InventoryEventWrapper<InventoryClickEvent>> playerInventoryClickHandler;
        private final Map<InventoryEventType, Consumer<InventoryEventWrapper<? extends InventoryEvent>>> defaultEventHandlers = new HashMap<>();
        private Consumer<InventoryEventWrapper<InventoryCloseEvent>> closeHandler;
        private Consumer<InventoryEventWrapper<InventoryOpenEvent>> openHandler;
        private final List<Function<BaseGui, List<ConditionalItem>>> filler = new ArrayList<>();
        private final List<ConditionalItem> border = new ArrayList<>();

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

        public Builder item(int slot, ConditionalItem item) {
            this.guiItemsMap.put(slot, item);
            return this;
        }

        public Builder items(Map<Integer, ConditionalItem> items) {
            this.guiItemsMap.putAll(items);
            return this;
        }

        public Builder repeatPageSlots(Collection<Integer> slots) {
            this.repeatPageSlots.addAll(slots);
            return this;
        }

        public Builder metadata(Object key, Object value) {
            this.metadata.set(key, value);
            return this;
        }

        public Builder metadata(Object key, Object value, boolean overwrite) {
            this.metadata.set(key, value, overwrite);
            return this;
        }

        public Builder metadata(Map<Object, Object> metadata, boolean overwrite) {
            this.metadata.putAll(metadata, overwrite);
            return this;
        }

        public Builder metadata(Metadata metadata, boolean overwrite) {
            this.metadata.putAll(metadata, overwrite);
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

        public Builder filler(List<Function<BaseGui, List<ConditionalItem>>> filler) {
            this.filler.addAll(filler);
            return this;
        }

        public Builder border(List<ConditionalItem> border) {
            this.border.addAll(border);
            return this;
        }

        public Builder border(ConditionalItem... border) {
            this.border.addAll(Arrays.asList(border));
            return this;
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public Gui build() {
            return new Gui(
                    this.id,
                    this.name,
                    this.rows,
                    this.guiItemsMap,
                    this.repeatPageSlots,
                    this.metadata,
                    this.playerInventoryClickHandler,
                    this.defaultEventHandlers,
                    this.closeHandler,
                    this.openHandler,
                    this.filler,
                    this.border
            );
        }

        public Builder copy() {
            return builder(this.id)
                    .name(this.name)
                    .rows(this.rows)
                    .items(this.guiItemsMap)
                    .repeatPageSlots(this.repeatPageSlots)
                    .metadata(this.metadata, true)
                    .playerInventoryClickHandler(this.playerInventoryClickHandler)
                    .defaultEventHandlers(this.defaultEventHandlers)
                    .closeHandler(this.closeHandler)
                    .openHandler(this.openHandler)
                    .filler(this.filler)
                    .border(this.border);
        }
    }
}
