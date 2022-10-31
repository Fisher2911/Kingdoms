package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.util.Metadata;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class SimpleConditionalItem extends ConditionalItem {

    private final BaseGuiItem item;

    public SimpleConditionalItem(BaseGuiItem item, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        this(item, Metadata.empty(), placeholders);
    }

    public SimpleConditionalItem(BaseGuiItem item, Metadata metadata, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        super(metadata, Collections.emptyList(), placeholders);
        this.item = item;
    }

    public SimpleConditionalItem(BaseGuiItem item) {
        this(item, Collections.emptyList());
    }

    @Override
    public BaseGuiItem getItem(Metadata metadata) {
        return this.item.withPlaceholders(this.placeholders);
    }

    public static class Builder extends ConditionalItem.Builder {

        private final BaseGuiItem item;

        public Builder(BaseGuiItem item) {
            super();
            this.item = item;
        }

        public Builder(SimpleConditionalItem item) {
            super(item);
            this.item = item.item.copy();
        }

        public Builder(ConditionalItem item, BaseGuiItem item1) {
            super(item);
            this.item = item1.copy();
        }

        public ConditionalItem build() {
            return new SimpleConditionalItem(this.item, this.metadata, this.placeholders);
        }

    }
}
