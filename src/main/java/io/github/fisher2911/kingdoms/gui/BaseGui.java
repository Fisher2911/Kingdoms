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

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import io.github.fisher2911.kingdoms.util.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseGui implements InventoryHolder {

    protected final String id;
    protected final String name;
    protected final int rows;
    protected final Map<Integer, ConditionalItem> guiItemsMap;
    private final Set<Integer> repeatPageSlots;
    protected final Inventory inventory;
    private int currentPage = 0;
    private final MapOfMaps<Integer, Integer, ConditionalItem> pageFillers;
    private final List<Function<BaseGui, List<ConditionalItem>>> fillers;
    private final List<ConditionalItem> border;
    private int maxPageSlot;
    protected final Metadata metadata;

    protected final Map<Integer, BaseGuiItem> currentInventoryItems;

    private static final Map<Object, Function<BaseGui, Object>> PLACEHOLDER_MAPPERS = Map.of(
            GuiKeys.ROLE_ID, gui -> {
                final Kingdom kingdom = gui.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                if (kingdom == null) return null;
                return kingdom.getRole(gui.getMetadata(GuiKeys.ROLE_ID, String.class));
            }
    );

    public BaseGui(
            String id,
            String name,
            int rows,
            Map<Integer, ConditionalItem> guiItemsMap,
            Set<Integer> repeatPageSlots,
            Metadata metadata,
            List<Function<BaseGui, List<ConditionalItem>>> fillers,
            List<ConditionalItem> border
    ) {
        this.metadata = metadata;
        this.metadata.set(GuiKeys.GUI, this);
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.guiItemsMap = guiItemsMap;
        final List<Object> placeholders = GuiKeys.toPlaceholders(this.metadata);
        /*this.metadata.get().keySet()
                .stream()
                .map(o -> {
                    final var function = PLACEHOLDER_MAPPERS.get(o);
                    if (function == null) return null;
                    return function.apply(this);
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());
        placeholders.add(this);*/

        this.repeatPageSlots = repeatPageSlots;
        this.inventory = Bukkit.createInventory(
                this,
                this.rows * 9,
                MessageHandler.serialize(PlaceholderBuilder.apply(this.name, placeholders.toArray()))
        );
        this.fillers = fillers;
        this.pageFillers = new MapOfMaps<>(new HashMap<>(), HashMap::new);
        this.initFillers();
        this.border = border;
        this.currentInventoryItems = new HashMap<>();
        this.reset();
    }

    public BaseGui(
            String id,
            String name,
            int rows,
            Map<Integer, ConditionalItem> guiItemsMap,
            Set<Integer> repeatPageSlots,
            List<Function<BaseGui, List<ConditionalItem>>> filler,
            List<ConditionalItem> border,
            Metadata metadata
    ) {
        this(id, name, rows, guiItemsMap, repeatPageSlots, metadata, filler, border);
    }

    public void open(HumanEntity human) {
        human.openInventory(this.inventory);
    }

    /**
     * clears the inventory then sets all items in {guiItemsMap} to the inventory
     */
    public void reset() {
        this.inventory.clear();
        this.currentInventoryItems.clear();
        this.setBorder();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            final ConditionalItem item;
            if (!this.repeatPageSlots.contains(i)) {
                item = this.guiItemsMap.get(this.getItemPageSlot(i));
            } else {
                item = this.guiItemsMap.get(i);
            }
            if (item == null) continue;
            this.setItem(i, item);
        }
        this.setFillers();
    }

    public void setFillers() {
        final Map<Integer, ConditionalItem> fillers = this.pageFillers.get(this.currentPage);
        if (fillers == null) return;
        for (var entry : fillers.entrySet()) {
            final int slot = entry.getKey();
            final ConditionalItem item = entry.getValue();
            this.setItem(slot, item);
        }
    }

    public void initFillers() {
        if (this.fillers.isEmpty()) return;
        this.pageFillers.clear();
        int currentSlot = 0;
        final List<ConditionalItem> fillers = this.fillers
                .stream()
                .map(function -> function.apply(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        for (ConditionalItem item : fillers) {
            while (
                    this.guiItemsMap.get(currentSlot) != null ||
                            this.isOnBorder(this.getIndexFromPageSlot(currentSlot)) ||
                            this.repeatPageSlots.contains(currentSlot)
            ) {
                currentSlot++;
            }
            final int page = this.getPageFromPageSlot(currentSlot);
            final Map<Integer, ConditionalItem> items = this.pageFillers.get(page);
            items.put(this.getIndexFromPageSlot(currentSlot), item);
            this.maxPageSlot = Math.max(this.maxPageSlot, currentSlot);
            currentSlot++;
        }
    }

    public void setBorder() {
        if (this.border.isEmpty()) return;
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (!this.isOnBorder(i)) continue;
            final BaseGuiItem item = this.border.get(i % this.border.size()).getItem(this.metadata);
            if (item == null) continue;
            this.currentInventoryItems.put(i, item);
            this.inventory.setItem(i, item.getItemStack(this));
        }
    }

    private boolean isOnBorder(int slot) {
        return slot < 9 || slot % 9 == 0 || slot % 9 == 8 || slot > this.inventory.getSize() - 9;
    }

    private void setItem(int slot, @Nullable ConditionalItem item) {
        if (slot >= this.inventory.getSize()) return;
        final int pageSlot = this.getItemPageSlot(slot);
        this.maxPageSlot = Math.max(this.maxPageSlot, pageSlot);
        if (item == null) {
            this.inventory.setItem(slot, null);
            this.currentInventoryItems.put(slot, null);
            this.guiItemsMap.remove(pageSlot);
            return;
        }
        final BaseGuiItem guiItem = item.getItem(this.metadata);
        if (guiItem == null) {
            this.inventory.setItem(slot, null);
            this.currentInventoryItems.put(slot, null);
            return;
        }
        this.inventory.setItem(slot, guiItem.getItemStack(this));
        this.currentInventoryItems.put(slot, guiItem);
    }

    public boolean hasNextPage() {
        return this.currentPage + 1 < this.getPageCount();
    }

    public int getPageCount() {
        return (int) Math.ceil((double) this.maxPageSlot / (this.rows * 9));
    }

    private void setItem(int slot) {
        final ConditionalItem item = this.guiItemsMap.get(this.getItemPageSlot(slot));
        this.setItem(slot, item);
    }

    public void refreshViewers() {
        for (HumanEntity viewer : this.inventory.getViewers()) {
            this.open(viewer);
        }
    }

    public void resetAndRefresh() {
        this.reset();
        this.refreshViewers();
    }

    public void setAndRefresh(int slot, ConditionalItem item) {
        this.set(slot, item);
        this.refreshViewers();
    }

    public void refresh(int slot) {
        final ConditionalItem item = this.getItem(slot);
        if (item == null) return;
        this.setItem(slot, item);
    }

    public void set(int slot, ConditionalItem item) {
        final ConditionalItem previous = this.guiItemsMap.put(slot, item);
        if (previous != null) this.initFillers();
        if (item != null) this.setItem(this.getItemPageSlot(slot), item);
    }

    public String getId() {
        return id;
    }

    @Nullable
    public ConditionalItem getItem(int slot) {
        return this.getItem(slot, true);
    }

    @Nullable
    public ConditionalItem getItem(int slot, boolean includeFillers) {
        final ConditionalItem item = this.guiItemsMap.get(slot);
        if (item != null) return item;
        if (!includeFillers) return null;
        final Map<Integer, ConditionalItem> fillers = this.pageFillers.get(this.currentPage);
        if (fillers == null) return null;
        return fillers.get(slot);
    }

    @Nullable
    public BaseGuiItem getBaseGuiItem(int slot) {
        final BaseGuiItem inSlot = this.currentInventoryItems.get(slot);
        if (inSlot != null) return inSlot;
        final ConditionalItem item = this.getItem(slot);
        if (item == null) return null;
        return item.getItem(this.metadata);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void goToNextPage() {
        if (!this.hasNextPage()) return;
        this.currentPage++;
        this.resetAndRefresh();
    }

    public void goToPreviousPage() {
        if (this.currentPage <= 0) return;
        this.currentPage--;
        this.resetAndRefresh();
    }

    public int getItemPageSlot(int slot) {
        return this.getPageFromPageSlot(slot) * (this.rows * 9) + slot;
    }

    public int getIndexFromPageSlot(int slot) {
        return slot - this.getPageFromPageSlot(slot) * (this.rows * 9);
    }

    public int getPageFromPageSlot(int slot) {
        return slot / (this.rows * 9);
    }

    public abstract void handleClick(InventoryClickEvent event);

    public abstract void handleDrag(InventoryDragEvent event);

    public abstract void handleClose(InventoryCloseEvent event);

    public abstract void handleOpen(InventoryOpenEvent event);

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void setMetadata(Object key, Object value) {
        this.metadata.set(key, value);
    }

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        return this.metadata.get(key, clazz);
    }

    @Nullable
    public Object getMetadata(Object key) {
        return this.metadata.get(key);
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
