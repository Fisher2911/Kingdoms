package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
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
    protected final Map<Integer, BaseGuiItem> guiItemsMap;
    private final Set<Integer> repeatPageSlots;
    protected final Inventory inventory;
    private int currentPage = 0;
    private final MapOfMaps<Integer, Integer, BaseGuiItem> pageFillers;
    private final List<Function<BaseGui, List<BaseGuiItem>>> fillers;
    private final List<BaseGuiItem> border;
    private int maxPageSlot;
    protected final Map<Object, Object> metadata;

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
            Map<Integer, BaseGuiItem> guiItemsMap,
            Set<Integer> repeatPageSlots,
            final Map<Object, Object> metadata,
            List<Function<BaseGui, List<BaseGuiItem>>> fillers,
            List<BaseGuiItem> border
    ) {
        this.metadata = metadata;
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.guiItemsMap = guiItemsMap;
        this.repeatPageSlots = repeatPageSlots;
        final List<Object> placeholders = this.metadata.keySet()
                .stream()
                .map(o -> {
                    final var function = PLACEHOLDER_MAPPERS.get(o);
                    if (function == null) return null;
                    return function.apply(this);
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());
        placeholders.add(this);
        this.inventory = Bukkit.createInventory(
                this,
                this.rows * 9,
                MessageHandler.serialize(PlaceholderBuilder.apply(this.name, placeholders.toArray()))
        );
        this.fillers = fillers;
        this.pageFillers = new MapOfMaps<>(new HashMap<>(), HashMap::new);
        this.initFillers();
        this.border = border;
        this.reset();
    }

    public BaseGui(
            String id,
            String name,
            int rows,
            Map<Integer, BaseGuiItem> guiItemsMap,
            Set<Integer> repeatPageSlots,
            List<Function<BaseGui, List<BaseGuiItem>>> filler,
            List<BaseGuiItem> border,
            final Map<Object, Object> metadata
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
        this.setBorder();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            final BaseGuiItem item;
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
        final Map<Integer, BaseGuiItem> fillers = this.pageFillers.get(this.currentPage);
        if (fillers == null) return;
        for (var entry : fillers.entrySet()) {
            final int slot = entry.getKey();
            final BaseGuiItem item = entry.getValue();
            this.setItem(slot, item);
        }
    }

    public void initFillers() {
        if (this.fillers.isEmpty()) return;
        this.pageFillers.clear();
        int currentSlot = 0;
        final List<BaseGuiItem> fillers = this.fillers
                .stream()
                .map(function -> function.apply(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        for (BaseGuiItem item : fillers) {
            while (
                    this.guiItemsMap.get(currentSlot) != null ||
                            this.isOnBorder(this.getIndexFromPageSlot(currentSlot)) ||
                            this.repeatPageSlots.contains(currentSlot)
            ) {
                currentSlot++;
            }
            final int page = this.getPageFromPageSlot(currentSlot);
            final Map<Integer, BaseGuiItem> items = this.pageFillers.get(page);
            items.put(this.getIndexFromPageSlot(currentSlot), item);
            this.maxPageSlot = Math.max(this.maxPageSlot, currentSlot);
            currentSlot++;
        }
    }

    public void setBorder() {
        if (this.border.isEmpty()) return;
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (!this.isOnBorder(i)) continue;
            this.inventory.setItem(i, this.border.get(i % this.border.size()).getItemStack(this));
        }
    }

    private boolean isOnBorder(int slot) {
        return slot < 9 || slot % 9 == 0 || slot % 9 == 8 || slot > this.inventory.getSize() - 9;
    }

    private void setItem(int slot, @Nullable BaseGuiItem item) {
        if (slot >= this.inventory.getSize()) return;
        final int pageSlot = this.getItemPageSlot(slot);
        this.maxPageSlot = Math.max(this.maxPageSlot, pageSlot);
        if (item == null) {
            this.inventory.setItem(slot, null);
            this.guiItemsMap.remove(pageSlot);
            return;
        }
        this.inventory.setItem(slot, item.getItemStack(this));
    }

    public boolean hasNextPage() {
        return this.currentPage + 1 < this.getPageCount();
    }

    public int getPageCount() {
        return (int) Math.ceil((double) this.maxPageSlot / (this.rows * 9));
    }

    private void setItem(int slot) {
        final BaseGuiItem item = this.guiItemsMap.get(this.getItemPageSlot(slot));
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

    public void setAndRefresh(int slot, BaseGuiItem item) {
        this.set(slot, item);
        this.refreshViewers();
    }

    public void refresh(int slot) {
        final BaseGuiItem item = this.getItem(slot);
        if (item == null) return;
        this.setItem(slot, item);
    }

    public void set(int slot, BaseGuiItem item) {
        final BaseGuiItem previous = this.guiItemsMap.put(slot, item);
        if (previous != null) this.initFillers();
        if (item != null) this.setItem(this.getItemPageSlot(slot), item);
    }

    public String getId() {
        return id;
    }

    @Nullable
    public BaseGuiItem getItem(int slot) {
        return this.getItem(slot, true);
    }

    @Nullable
    public BaseGuiItem getItem(int slot, boolean includeFillers) {
        final BaseGuiItem item = this.guiItemsMap.get(slot);
        if (item != null) return item;
        if (!includeFillers) return null;
        final Map<Integer, BaseGuiItem> fillers = this.pageFillers.get(this.currentPage);
        if (fillers == null) return null;
        return fillers.get(slot);
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
        this.metadata.put(key, value);
    }

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        final Object o = this.metadata.get(key);
        if (o == null) return null;
        if (!clazz.isInstance(o)) return null;
        return clazz.cast(o);
    }

    @Nullable
    public Object getMetadata(Object key) {
        return this.metadata.get(key);
    }

    public Map<Object, Object> getMetadata() {
        return metadata;
    }
}
