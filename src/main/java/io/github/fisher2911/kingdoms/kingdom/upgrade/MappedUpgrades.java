/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

}
