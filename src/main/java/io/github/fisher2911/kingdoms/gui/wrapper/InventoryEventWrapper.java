package io.github.fisher2911.kingdoms.gui.wrapper;

import io.github.fisher2911.kingdoms.gui.BaseGui;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryEvent;

public class InventoryEventWrapper<T extends InventoryEvent> {

    private final BaseGui gui;
    private final T event;

    public InventoryEventWrapper(BaseGui gui, T event) {
        this.gui = gui;
        this.event = event;
    }

    public static <T extends InventoryEvent> InventoryEventWrapper<T> wrap(BaseGui gui, T event) {
        return new InventoryEventWrapper<T>(gui, event);
    }

    public void cancel(boolean cancel) {
        if (event instanceof Cancellable cancellable) cancellable.setCancelled(cancel);
    }

    public void cancel() {
        this.cancel(true);
    }

    public BaseGui gui() {
        return this.gui;
    }

    public T event() {
        return this.event;
    }

}
