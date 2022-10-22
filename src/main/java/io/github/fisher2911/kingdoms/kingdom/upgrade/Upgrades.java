package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import org.jetbrains.annotations.Nullable;

public interface Upgrades<T> {

    String getId();
    @Nullable
    T getValueAtLevel(int level);
    @Nullable
    Price getPriceAtLevel(int level);
    int getMaxLevel();

}
