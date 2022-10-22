package io.github.fisher2911.kingdoms.gui;

import net.kyori.adventure.text.Component;
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

import java.util.Map;

public abstract class BaseGui implements InventoryHolder {

    protected final String name;
    protected final int rows;
    protected final Map<Integer, BaseGuiItem> guiItemsMap;
    protected final Inventory inventory;

    public BaseGui(String name, int rows, Map<Integer, BaseGuiItem> guiItemsMap) {
        this.name = name;
        this.rows = rows;
        this.guiItemsMap = guiItemsMap;
        this.inventory = Bukkit.createInventory(this, this.rows * 9, Component.text(this.name));
        this.reset();
    }

    public void open(HumanEntity human) {
        human.openInventory(this.inventory);
    }

    /**
     * clears the inventory then sets all items in {guiItemsMap} to the inventory
     */
    public void reset() {
        this.inventory.clear();
        for (var entry : this.guiItemsMap.entrySet()) {
            this.inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
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
        this.inventory.setItem(slot, item.getItemStack());
    }

    public void set(int slot, BaseGuiItem item) {
        this.guiItemsMap.put(slot, item);
        if (item != null) this.inventory.setItem(slot, item.getItemStack());
    }

    @Nullable
    public BaseGuiItem getItem(int slot) {
        return this.guiItemsMap.get(slot);
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
