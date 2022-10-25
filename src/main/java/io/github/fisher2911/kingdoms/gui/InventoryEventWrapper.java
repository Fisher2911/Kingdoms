package io.github.fisher2911.kingdoms.gui;

import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryEvent;

public record InventoryEventWrapper<T extends InventoryEvent>(BaseGui gui, T event) {

    public static <T extends InventoryEvent> InventoryEventWrapper<T> wrap(BaseGui gui, T event) {
        return new InventoryEventWrapper<T>(gui, event);
    }

    public void cancel(boolean cancel) {
        if (event instanceof Cancellable cancellable) cancellable.setCancelled(cancel);
    }

    public void cancel() {
        this.cancel(true);
    }

}
