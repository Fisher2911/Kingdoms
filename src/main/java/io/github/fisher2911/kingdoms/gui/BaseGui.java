package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
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

import java.util.List;
import java.util.Map;

public abstract class BaseGui implements InventoryHolder {

    protected final String id;
    protected final String name;
    protected final int rows;
    protected final Map<Integer, BaseGuiItem> guiItemsMap;
    protected final Inventory inventory;
    private int currentPage = 0;
    private final List<BaseGuiItem> fillers;
    private final List<BaseGuiItem> border;
    protected final int nextPageItemSlot;
    protected final int previousPageItemSlot;
    @Nullable
    private final GuiItem nextPageItem;
    @Nullable
    private final GuiItem previousPageItem;
    private int maxPageSlot;

    public BaseGui(
            String id,
            String name,
            int rows,
            Map<Integer, BaseGuiItem> guiItemsMap,
            List<BaseGuiItem> fillers,
            List<BaseGuiItem> border,
            int nextPageItemSlot,
            @Nullable GuiItem nextPageItem,
            int previousPageItemSlot,
            @Nullable GuiItem previousPageItem
    ) {
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.guiItemsMap = guiItemsMap;
        this.inventory = Bukkit.createInventory(
                this,
                this.rows * 9,
                PlaceholderBuilder.apply(MessageHandler.serialize(this.name), this)
        );
        this.fillers = fillers;
        this.border = border;
        this.nextPageItemSlot = nextPageItemSlot;
        this.nextPageItem = nextPageItem;
        this.previousPageItemSlot = previousPageItemSlot;
        this.previousPageItem = previousPageItem;
        this.reset();
    }

    public BaseGui(String id, String name, int rows, Map<Integer, BaseGuiItem> guiItemsMap, @Nullable BaseGuiItem filler, List<BaseGuiItem> border) {
        this(id, name, rows, guiItemsMap, filler, border, -1, null, -1, null);
    }

    public void open(HumanEntity human) {
        human.openInventory(this.inventory);
    }

    /**
     * clears the inventory then sets all items in {guiItemsMap} to the inventory
     */
    public void reset() {
        this.inventory.clear();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            final BaseGuiItem item = this.guiItemsMap.get(this.getItemPageSlot(i));
            if (item == null) continue;
            this.setItem(i, item);
        }
    }

    public void setFiller() {
        if (this.filler == null) return;
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.guiItemsMap.containsKey(this.getItemPageSlot(i))) continue;
            if (this.isOnBorder(i) && !this.border.isEmpty()) continue;
            this.setItem(i, this.filler);
        }
    }

    public void setBorder() {
        if (this.border.isEmpty()) return;
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (!this.isOnBorder(i)) continue;
            this.set(i, this.border.get(i % this.border.size()));
        }
    }

    private boolean isOnBorder(int slot) {
        return slot < 9 || slot > 44 || slot % 9 == 0 || slot % 9 == 8;
    }

    private void setItem(int slot, @Nullable BaseGuiItem item) {
        final int pageSlot = this.getItemPageSlot(slot);
        this.maxPageSlot = Math.max(this.maxPageSlot, pageSlot);
        if (slot == this.nextPageItemSlot && this.nextPageItem != null) {
            this.inventory.setItem(slot, this.nextPageItem.getItemStack());
            return;
        }
        if (slot == this.previousPageItemSlot && this.previousPageItem != null) {
            this.inventory.setItem(slot, this.previousPageItem.getItemStack());
            return;
        }
        if (item == null) {
            this.inventory.setItem(slot, null);
            this.guiItemsMap.remove(pageSlot);
            return;
        }
        this.inventory.setItem(slot, item.getItemStack());
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
        final BaseGuiItem item = this.guiItemsMap.get(slot);
        if (item == null) return;
        this.setItem(slot, item);
    }

    public void set(int slot, BaseGuiItem item) {
        this.guiItemsMap.put(slot, item);
        if (item != null) this.setItem(this.getItemPageSlot(slot), item);
    }

    public String getId() {
        return id;
    }

    @Nullable
    public BaseGuiItem getItem(int slot) {
        return this.guiItemsMap.get(this.getItemPageSlot(slot));
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
        return slot + (this.currentPage * this.inventory.getSize());
    }

    public int getIndexFromSlot(int slot) {
        return slot - (this.currentPage * this.inventory.getSize());
    }

    public abstract void handleClick(InventoryClickEvent event);

    public abstract void handleDrag(InventoryDragEvent event);

    public abstract void handleClose(InventoryCloseEvent event);

    public abstract void handleOpen(InventoryOpenEvent event);

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
