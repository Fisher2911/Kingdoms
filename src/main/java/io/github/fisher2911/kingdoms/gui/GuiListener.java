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

import io.github.fisher2911.kingdoms.listener.GlobalListener;
import io.github.fisher2911.kingdoms.listener.KListener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class GuiListener extends KListener {

    public GuiListener(GlobalListener globalListener) {
        super(globalListener);
    }

    @Override
    public void init() {
        this.globalListener.register(InventoryOpenEvent.class, this::onOpen);
        this.globalListener.register(InventoryCloseEvent.class, this::onClose);
        this.globalListener.register(InventoryClickEvent.class, this::onClick);
        this.globalListener.register(InventoryDragEvent.class, this::onDrag);
    }

    public void onClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        final InventoryView view = event.getView();
        if (inventory == null) return;
        if (inventory.getHolder() instanceof final BaseGui gui) {
            gui.handleClick(event);
            return;
        }
        if (view.getTopInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleClick(event);
        }
    }

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

    public void onOpen(InventoryOpenEvent event) {
        final InventoryView view = event.getView();
        if (view.getTopInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleOpen(event);
        }
    }

    public void onClose(InventoryCloseEvent event) {
        final InventoryView view = event.getView();
        if (view.getTopInventory().getHolder() instanceof final BaseGui gui) {
            gui.handleClose(event);
        }
    }

}
