package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import org.jetbrains.annotations.Nullable;

public interface Upgrades<T> {

    String getId();
    @Nullable
    T getValueAtLevel(int level);
    String getDisplayValueAtLevel(int level);
    @Nullable
    Price getPriceAtLevel(int level);
    String getDisplayName();
    int getMaxLevel();

}
