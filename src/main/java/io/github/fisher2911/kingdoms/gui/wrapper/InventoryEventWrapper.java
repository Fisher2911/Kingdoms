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
