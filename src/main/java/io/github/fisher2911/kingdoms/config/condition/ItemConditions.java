package io.github.fisher2911.kingdoms.config.condition;

import io.github.fisher2911.kingdoms.gui.ConditionalItem;
import io.github.fisher2911.kingdoms.util.Metadata;

import java.util.List;

public class ItemConditions implements ItemConditional {

    private final List<MetadataPredicate> conditionalList;
    private final ConditionalItem item;

    public static ItemConditions alwaysTrue(ConditionalItem item) {
        return new ItemConditions(List.of(), item);
    }

    public ItemConditions(List<MetadataPredicate> conditionalList, ConditionalItem item) {
        this.conditionalList = conditionalList;
        this.item = item;
    }

    public boolean test(Metadata possible) {
        for (final MetadataPredicate conditional : this.conditionalList) {
            if (!conditional.test(possible)) {
                return false;
            }
        }
        return true;
    }

    public List<MetadataPredicate> getConditionalList() {
        return conditionalList;
    }

    public ConditionalItem getItem() {
        return item;
    }
}
