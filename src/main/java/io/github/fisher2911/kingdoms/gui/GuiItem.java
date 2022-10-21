package io.github.fisher2911.kingdoms.gui;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class GuiItem extends BaseGuiItem {

    private final Map<ClickType, Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers;
    @Nullable
    private final Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;

    public GuiItem(
            ItemStack itemStack,
            Map<ClickType, Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers,
            @Nullable Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler
    ) {
        super(itemStack);
        this.clickHandlers = clickHandlers;
        this.dragHandler = dragHandler;
    }

    @Override
    public BaseGuiItem withItemStack(ItemStack itemStack) {
        return new GuiItem(itemStack, this.clickHandlers, this.dragHandler);
    }

    @Override
    public void handleClick(InventoryEventWrapper<InventoryClickEvent> event) {
        final Consumer<InventoryEventWrapper<InventoryClickEvent>> handler = this.clickHandlers.get(event.event().getClick());
        if (handler == null) return;
        handler.accept(event);
    }

    @Override
    public void handleDrag(InventoryEventWrapper<InventoryDragEvent> event) {
        if (this.dragHandler == null) return;
        this.dragHandler.accept(event);
    }

    public static class Builder {

        private final ItemStack itemStack;
        private Map<ClickType, Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers;
        @Nullable
        private Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;

        private Builder(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public static Builder of(ItemStack itemStack) {
            return new Builder(itemStack);
        }

        public Builder clickHandler(ClickType clickType, Consumer<InventoryEventWrapper<InventoryClickEvent>> handler) {
            this.clickHandlers.put(clickType, handler);
            return this;
        }

        public Builder dragHandler(Consumer<InventoryEventWrapper<InventoryDragEvent>> handler) {
            this.dragHandler = handler;
            return this;
        }

        public GuiItem build() {
            return new GuiItem(this.itemStack, this.clickHandlers, this.dragHandler);
        }
    }
}
