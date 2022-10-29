package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.kingdoms.util.ArrayUtil;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class BaseGuiItem {

    protected final ItemBuilder itemBuilder;
    protected final Map<Object, Object> metadata;
    protected final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders;

    public BaseGuiItem(ItemBuilder itemBuilder, Map<Object, Object> metadata, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        this.itemBuilder = itemBuilder;
        this.metadata = metadata;
        this.placeholders = placeholders;
    }

    public abstract BaseGuiItem withItem(ItemBuilder item);
    public abstract BaseGuiItem withItem(ItemStack item);
    public abstract void handleClick(InventoryEventWrapper<InventoryClickEvent> wrapper);
    public abstract void handleDrag(InventoryEventWrapper<InventoryDragEvent> event);

    public ItemStack getItemStack(BaseGui gui, Object... placeholders) {
        if (placeholders.length == 0) return this.itemBuilder.build(this.getPlaceholdersAsArray(gui));
        if (this.placeholders.size() == 0) return this.itemBuilder.build(placeholders);
        return this.itemBuilder.build(ArrayUtil.combine(this.getPlaceholdersAsArray(gui), placeholders));
    }

    protected Object[] getPlaceholdersAsArray(BaseGui gui) {
        return this.placeholders.stream().map(s -> s.apply(gui, this)).toArray();
    }

    public abstract BaseGuiItem withPlaceholders(List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders);

    public abstract BaseGuiItem copy();

//    public

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        final Object o = this.metadata.get(key);
        if (o == null) return null;
        if (!clazz.isInstance(o)) return null;
        return clazz.cast(o);
    }

    @Nullable
    public Object getMetadata(Object key) {
        return this.metadata.get(key);
    }

    public Map<Object, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Object key, Object value) {
        this.metadata.put(key, value);
    }

    public void setMetadata(Map<Object, Object> metadata) {
        this.metadata.putAll(metadata);
    }

    @Override
    public String toString() {
        return "BaseGuiItem{" +
                "itemBuilder=" + itemBuilder +
//                ", placeholders=" + Arrays.toString(this.getPlaceholdersAsArray()) +
                ", metadata=" + metadata +
                '}';
    }

}
