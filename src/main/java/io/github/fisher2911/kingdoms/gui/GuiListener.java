package io.github.fisher2911.kingdoms.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        final InventoryView view = event.getView();
        if (inventory == null) return;
        if (inventory.getHolder() instanceof final BaseGui gui) {
            gui.handleClick(event);
            return;
        }
        if (view.getBottomInventory().equals(inventory) && view.getTopInventory() instanceof final BaseGui gui) {
            gui.handleClick(event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        final InventoryView view = event.getView();
        if (view.getTopInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleDrag(event);
            return;
        }
        if (view.getBottomInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleDrag(event);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        final InventoryView view = event.getView();
        if (view.getTopInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleOpen(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        final InventoryView view = event.getView();
        if (view.getTopInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleClose(event);
        }
    }

}
