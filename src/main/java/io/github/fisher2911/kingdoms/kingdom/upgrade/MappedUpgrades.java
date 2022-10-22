package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MappedUpgrades<T> implements Upgrades<T>{

    private final String id;
    private final Map<Integer, UpgradeData<T>> levelUpgrades;
    private int maxLevel;

    public MappedUpgrades(String id, Map<Integer, UpgradeData<T>> levelUpgrades, int maxLevel) {
        this.id = id;
        this.levelUpgrades = levelUpgrades;
        this.maxLevel = maxLevel;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Nullable
    public T getValueAtLevel(int level) {
        if (level > this.maxLevel) return null;
        final UpgradeData<T> data = this.levelUpgrades.get(level);
        if (data == null) return null;
        return data.getValue();
    }

    @Override
    @Nullable
    public Price getPriceAtLevel(int level) {
        if (level > this.maxLevel) return null;
        final UpgradeData<T> data = this.levelUpgrades.get(level);
        if (data == null) return null;
        return data.getPrice();
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }
}
