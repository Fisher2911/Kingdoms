package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiItem extends BaseGuiItem {

    private final Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler;
    @Nullable
    private final Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;

    public GuiItem(
            ItemBuilder itemBuilder,
            Map<Object, Object> metadata,
            @Nullable Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler,
            @Nullable Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler,
            List<Supplier<Object>> placeholders
    ) {
        super(itemBuilder, metadata, placeholders);
        this.clickHandler = clickHandler;
        this.dragHandler = dragHandler;
    }

    @Override
    public BaseGuiItem withItem(ItemBuilder item) {
        return new GuiItem(item, new HashMap<>(this.metadata), this.clickHandler, this.dragHandler, this.placeholders);
    }

    @Override
    public BaseGuiItem withItem(ItemStack item) {
        return this.withItem(ItemBuilder.from(item));
    }

    @Override
    public void handleClick(InventoryEventWrapper<InventoryClickEvent> event) {
        if (this.clickHandler == null) return;
        this.clickHandler.accept(event);
    }

    @Override
    public void handleDrag(InventoryEventWrapper<InventoryDragEvent> event) {
        if (this.dragHandler == null) return;
        this.dragHandler.accept(event);
    }

    public static GuiItem nextPage(ItemBuilder itemBuilder, Collection<ClickType> clickTypes) {
        return new GuiItem(itemBuilder, new HashMap<>(), nextPageWrapper(clickTypes), null, new ArrayList<>());
    }

    public static GuiItem nextPage(ItemBuilder itemBuilder) {
        return nextPage(itemBuilder, List.of(ClickType.values()));
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> nextPageWrapper(Collection<ClickType> clickTypes) {
        return event -> {
            if (!clickTypes.contains(event.event().getClick())) return;
            event.gui().goToNextPage();
        };
    }

    public static GuiItem previousPage(ItemBuilder itemBuilder, Collection<ClickType> clickTypes) {
        return new GuiItem(itemBuilder, new HashMap<>(), previousPageWrapper(clickTypes), null, new ArrayList<>());
    }

    public static GuiItem previousPage(ItemBuilder itemBuilder) {
        return previousPage(itemBuilder, List.of(ClickType.values()));
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> previousPageWrapper(Collection<ClickType> clickTypes) {
        return event -> {
            if (!clickTypes.contains(event.event().getClick())) return;
            event.gui().goToPreviousPage();
        };
    }

    @Override
    public BaseGuiItem withPlaceholders(List<Supplier<Object>> placeholders) {
        return new GuiItem(this.itemBuilder, new HashMap<>(this.metadata), this.clickHandler, this.dragHandler, placeholders);
    }

    @Override
    public BaseGuiItem copy() {
        return new GuiItem(this.itemBuilder, new HashMap<>(this.metadata), this.clickHandler, this.dragHandler, this.placeholders);
    }

    public static Builder builder(ItemBuilder itemBuilder) {
        return Builder.of(itemBuilder);
    }

    public static Builder builder(GuiItem guiItem) {
        return Builder.of(guiItem);
    }

    public static class Builder {

        private final ItemBuilder itemBuilder;
        private Map<Object, Object> metadata;
        private Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler;
        @Nullable
        private Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;
        private final List<Supplier<Object>> placeholders = new ArrayList<>();

        private Builder(ItemBuilder itemBuilder) {
            this.itemBuilder = itemBuilder;
        }

        private Builder(GuiItem guiItem) {
            this.itemBuilder = guiItem.itemBuilder;
            this.metadata = guiItem.metadata;
            this.clickHandler = guiItem.clickHandler;
            this.dragHandler = guiItem.dragHandler;
            this.placeholders.addAll(guiItem.placeholders);
        }

        private static Builder of(ItemBuilder itemBuilder) {
            return new Builder(itemBuilder);
        }

        private static Builder of(GuiItem guiItem) {
            return new Builder(guiItem);
        }

        public Builder metadata(Map<Object, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder clickHandler(Consumer<InventoryEventWrapper<InventoryClickEvent>> handler) {
            this.clickHandler = handler;
            return this;
        }

        public Builder dragHandler(Consumer<InventoryEventWrapper<InventoryDragEvent>> handler) {
            this.dragHandler = handler;
            return this;
        }

        public Builder placeholder(Supplier<Object> placeholder) {
            this.placeholders.add(placeholder);
            return this;
        }

        public Builder placeholders(Collection<Supplier<Object>> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        public GuiItem build() {
            if (this.metadata == null) this.metadata = new HashMap<>();
            return new GuiItem(this.itemBuilder, this.metadata, this.clickHandler, this.dragHandler, this.placeholders);
        }
    }
}
