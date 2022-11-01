package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MappedUpgrades<T> implements Upgrades<T>{

    private final String id;
    private final String displayName;
    private final Map<Integer, UpgradeData<T>> levelUpgrades;
    private final int maxLevel;
    private final BaseItemBuilder displayItem;
    private final BaseItemBuilder maxLevelDisplayItem;

    public MappedUpgrades(String id, String displayName, Map<Integer, UpgradeData<T>> levelUpgrades, int maxLevel, BaseItemBuilder displayItem, BaseItemBuilder maxLevelDisplayItem) {
        this.id = id;
        this.displayName = displayName;
        this.levelUpgrades = levelUpgrades;
        this.maxLevel = maxLevel;
        this.displayItem = displayItem;
        this.maxLevelDisplayItem = maxLevelDisplayItem;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
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
    public String getDisplayValueAtLevel(int level) {
        if (level > this.maxLevel) return null;
        final UpgradeData<T> data = this.levelUpgrades.get(level);
        if (data == null) return null;
        return data.getDisplayValue();
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

    @Override
    public BaseItemBuilder getGuiItem() {
        return this.displayItem;
    }

    @Override
    public BaseItemBuilder getMaxLevelGuiItem() {
        return this.maxLevelDisplayItem;
    }
}
