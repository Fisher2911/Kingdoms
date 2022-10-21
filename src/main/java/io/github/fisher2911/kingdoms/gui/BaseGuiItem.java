package io.github.fisher2911.kingdoms.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BaseGuiItem {

    protected final ItemStack itemStack;

    public BaseGuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public abstract BaseGuiItem withItemStack(ItemStack itemStack);
    public abstract void handleClick(InventoryEventWrapper<InventoryClickEvent> wrapper);
    public abstract void handleDrag(InventoryEventWrapper<InventoryDragEvent> event);

    public ItemStack getItemStack() {
        return this.itemStack.clone();
    }
}
