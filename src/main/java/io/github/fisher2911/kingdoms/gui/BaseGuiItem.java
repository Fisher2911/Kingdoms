package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.util.ArrayUtil;
import io.github.fisher2911.kingdoms.util.Metadata;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public abstract class BaseGuiItem {

    protected final BaseItemBuilder itemBuilder;
    protected final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders;
    protected final Metadata metadata;

    public BaseGuiItem(BaseItemBuilder itemBuilder, Metadata metadata, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        this.itemBuilder = itemBuilder;
        this.metadata = metadata;
        this.placeholders = placeholders;
    }

    public abstract BaseGuiItem withItem(BaseItemBuilder item);
    public abstract BaseGuiItem withItem(ItemStack item);
    public abstract void handleClick(InventoryEventWrapper<InventoryClickEvent> wrapper);
    public abstract void handleDrag(InventoryEventWrapper<InventoryDragEvent> event);

    public ItemStack getItemStack(BaseGui gui, Object... placeholders) {
        if (placeholders.length == 0) return this.itemBuilder.build(this.getPlaceholdersAsArray(this.metadata));
        if (this.placeholders.size() == 0) return this.itemBuilder.build(GuiKeys.toPlaceholders(
                this.metadata.copyWith(gui.getMetadata(), false)
        ).toArray());
        return this.itemBuilder.build(ArrayUtil.combine(this.getPlaceholdersAsArray(metadata), placeholders));
    }

    protected Object[] getPlaceholdersAsArray(Metadata metadata) {
        return GuiKeys.toPlaceholders(metadata).toArray();
    }

    public abstract BaseGuiItem withPlaceholders(List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders);

    public abstract BaseGuiItem withMetaData(Metadata metadata, boolean overWrite);

    public abstract BaseGuiItem copy();

    public void setMetadata(Object key, Object value) {
        this.metadata.set(key, value);
    }

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        return this.metadata.get(key, clazz);
    }

    @Nullable
    public Object getMetadata(Object key) {
        return this.metadata.get(key);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "BaseGuiItem{" +
                "itemBuilder=" + itemBuilder +
                ", metadata=" + metadata +
                '}';
    }

}
