package io.github.fisher2911.kingdoms.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        final InventoryView view = event.getView();
        if (inventory.getHolder() instanceof final BaseGui gui) {
            gui.handleClick(event);
            return;
        }
        if (view.getBottomInventory().equals(inventory) && view.getTopInventory() instanceof final BaseGui gui) {
            gui.handleClick(event);
        }
    }

}
