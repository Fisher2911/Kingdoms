package io.github.fisher2911.kingdoms.config.condition;

import io.github.fisher2911.kingdoms.gui.ConditionalItem;
import io.github.fisher2911.kingdoms.util.Metadata;

public interface ItemConditional extends MetadataPredicate {

    boolean test(Metadata metadata);
    ConditionalItem getItem();

}
