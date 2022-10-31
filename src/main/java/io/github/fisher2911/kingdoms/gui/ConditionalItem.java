package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.config.condition.ItemConditional;
import io.github.fisher2911.kingdoms.util.Metadata;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ConditionalItem {

    private final Metadata metadata;
    private final List<ItemConditional> conditionalItems;
    protected final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders;

    protected ConditionalItem(Metadata metadata, List<ItemConditional> conditionalItems, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        this.metadata = metadata;
        this.conditionalItems = conditionalItems;
        this.placeholders = placeholders;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        return this.metadata.getMetadata(key, clazz);
    }

    @Nullable
    public Object getMetadata(Object key) {
        return this.metadata.get(key);
    }

    public List<ItemConditional> getConditionalItems() {
        return conditionalItems;
    }

    public List<BiFunction<BaseGui, BaseGuiItem, Object>> getPlaceholders() {
        return placeholders;
    }

    public BaseGuiItem getItem(Metadata metadata) {
        final Metadata full = metadata.copyWith(this.metadata);
        for (final ItemConditional conditions : this.conditionalItems) {
            if (conditions.test(full)) {
                return conditions.getItem().getItem(full).withPlaceholders(this.placeholders).withMetaData(full);
            }
        }
        return GuiItem.air();
    }

    public static ConditionalItem of(BaseGuiItem item) {
        return new SimpleConditionalItem(item);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ConditionalItem item) {
        if (item instanceof final SimpleConditionalItem simpleConditionalItem) {
            return new SimpleConditionalItem.Builder(simpleConditionalItem);
        }
        return new Builder(item);
    }

    public static Builder builder(BaseGuiItem item) {
        return new SimpleConditionalItem.Builder(item);
    }

    public static class Builder {

        protected final Metadata metadata;
        protected final List<ItemConditional> conditionalItems;
        protected final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders;

        protected Builder() {
            this.metadata = new Metadata(new HashMap<>());
            this.conditionalItems = new ArrayList<>();
            this.placeholders = new ArrayList<>();
        }

        protected Builder(ConditionalItem item) {
            this.metadata = item.metadata.copy();
            this.conditionalItems = new ArrayList<>(item.conditionalItems);
            this.placeholders = new ArrayList<>(item.placeholders);
        }

        public Builder metadata(Map<Object, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder metadata(Object key, Object value) {
            this.metadata.set(key, value);
            return this;
        }

        public Builder addConditionalItem(ItemConditional itemConditions) {
            this.conditionalItems.add(itemConditions);
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

        public ConditionalItem build() {
            return new ConditionalItem(this.metadata, this.conditionalItems, this.placeholders);
        }

        public ConditionalItem build(BaseGuiItem item) {
            return new SimpleConditionalItem(item, this.metadata, this.placeholders);
        }

        public Metadata getMetadata() {
            return metadata;
        }
    }
}
