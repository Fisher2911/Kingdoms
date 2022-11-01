package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.Metadata;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import org.bukkit.Material;
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
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GuiItem extends BaseGuiItem {

    private final Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler;
    @Nullable
    private final Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;

    public GuiItem(
            BaseItemBuilder itemBuilder,
            Metadata metadata,
            @Nullable Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler,
            @Nullable Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler,
            List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders
    ) {
        super(itemBuilder, metadata, placeholders);
        this.clickHandler = clickHandler;
        this.dragHandler = dragHandler;
    }

    @Override
    public BaseGuiItem withItem(BaseItemBuilder item) {
        return new GuiItem(item, this.metadata.copy(), this.clickHandler, this.dragHandler, this.placeholders);
    }

    @Override
    public BaseGuiItem withItem(ItemStack item) {
        return this.withItem(BaseItemBuilder.from(item));
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

    public static GuiItem nextPage(BaseItemBuilder itemBuilder, Collection<ClickType> clickTypes) {
        return new GuiItem(itemBuilder, new Metadata(new HashMap<>()), nextPageWrapper(clickTypes), null, new ArrayList<>());
    }

    public static GuiItem nextPage(BaseItemBuilder itemBuilder) {
        return nextPage(itemBuilder, List.of(ClickType.values()));
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> nextPageWrapper(Collection<ClickType> clickTypes) {
        return event -> {
            if (!clickTypes.contains(event.event().getClick())) return;
            event.gui().goToNextPage();
        };
    }

    public static GuiItem previousPage(BaseItemBuilder itemBuilder, Collection<ClickType> clickTypes) {
        return new GuiItem(itemBuilder, new Metadata(new HashMap<>()), previousPageWrapper(clickTypes), null, new ArrayList<>());
    }

    public static GuiItem previousPage(BaseItemBuilder itemBuilder) {
        return previousPage(itemBuilder, List.of(ClickType.values()));
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> previousPageWrapper(Collection<ClickType> clickTypes) {
        return event -> {
            if (!clickTypes.contains(event.event().getClick())) return;
            event.gui().goToPreviousPage();
        };
    }

    @Override
    public BaseGuiItem withPlaceholders(List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        return new GuiItem(this.itemBuilder, this.metadata.copy(), this.clickHandler, this.dragHandler, placeholders);
    }

    @Override
    public BaseGuiItem withMetaData(Metadata metadata, boolean overwrite) {
        final User user = metadata.get(GuiKeys.KINGDOM_MEMBER, User.class);
        return new GuiItem(this.itemBuilder, this.metadata.copyWith(metadata, overwrite), this.clickHandler, this.dragHandler, this.placeholders);
    }

    @Override
    public BaseGuiItem copy() {
        return new GuiItem(this.itemBuilder, this.metadata.copy(), this.clickHandler, this.dragHandler, this.placeholders);
    }

    public static BaseGuiItem air() {
        return new GuiItem(BaseItemBuilder.from(Material.AIR), Metadata.empty(), InventoryEventWrapper::cancel, InventoryEventWrapper::cancel, new ArrayList<>());
    }

    public static Builder builder(BaseItemBuilder itemBuilder) {
        return Builder.of(itemBuilder);
    }

    public static Builder builder(GuiItem guiItem) {
        return Builder.of(guiItem);
    }

    public static Builder builder(BaseGuiItem guiItem) {
        return Builder.of(guiItem);
    }

    public static class Builder {

        private final BaseItemBuilder itemBuilder;
        private final Metadata metadata = new Metadata(new HashMap<>());
        private Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler;
        @Nullable
        private Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;
        private final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders = new ArrayList<>();

        private Builder(BaseItemBuilder itemBuilder) {
            this.itemBuilder = itemBuilder;
        }

        private Builder(GuiItem guiItem) {
            this.itemBuilder = guiItem.itemBuilder;
            this.metadata.putAll(guiItem.metadata, true);
            this.clickHandler = guiItem.clickHandler;
            this.dragHandler = guiItem.dragHandler;
            this.placeholders.addAll(guiItem.placeholders);
        }

        private Builder(BaseGuiItem guiItem) {
            this.itemBuilder = guiItem.itemBuilder;
            this.metadata.putAll(guiItem.metadata, true);
            this.placeholders.addAll(guiItem.placeholders);
            if (guiItem instanceof GuiItem item) {
                this.clickHandler = item.clickHandler;
                this.dragHandler = item.dragHandler;
            }
        }

        private static Builder of(BaseItemBuilder itemBuilder) {
            return new Builder(itemBuilder);
        }

        private static Builder of(GuiItem guiItem) {
            return new Builder(guiItem);
        }

        private static Builder of(BaseGuiItem guiItem) {
            return new Builder(guiItem);
        }

        public Builder clickHandler(Consumer<InventoryEventWrapper<InventoryClickEvent>> handler) {
            this.clickHandler = handler;
            return this;
        }

        public Builder dragHandler(Consumer<InventoryEventWrapper<InventoryDragEvent>> handler) {
            this.dragHandler = handler;
            return this;
        }

        public Builder placeholder(BiFunction<BaseGui, BaseGuiItem, Object> placeholder) {
            this.placeholders.add(placeholder);
            return this;
        }

        public Builder placeholders(Collection<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        public Builder metadata(Object key, Object value) {
            this.metadata.set(key, value);
            return this;
        }

        public Builder metadata(Map<Object, Object> metadata, boolean overwrite) {
            this.metadata.putAll(metadata, overwrite);
            return this;
        }

        public GuiItem build() {
            return new GuiItem(this.itemBuilder, this.metadata, this.clickHandler, this.dragHandler, this.placeholders);
        }
    }
}
