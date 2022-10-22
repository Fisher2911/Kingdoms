package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UpgradeHolder {

    private final Map<String, Upgrades<?>> upgradesMap;
    private final Set<EntryUpgrade> entryUpgrades;

    public UpgradeHolder(Map<String, Upgrades<?>> upgradesMap, Set<EntryUpgrade> entryUpgrades) {
        this.upgradesMap = upgradesMap;
        this.entryUpgrades = entryUpgrades;
    }

    @Nullable
    public <T> Upgrades<T> getUpgrades(String id, Class<? extends Upgrades<T>> clazz) {
        final Object o = this.upgradesMap.get(id);
        if (o == null) return null;
        if (!clazz.isInstance(o)) return null;
        return clazz.cast(o);
    }

    @Nullable
    public <T> T getValueAtLevel(String id, Class<? extends Upgrades<T>> clazz, int level) {
        final Upgrades<T> upgrades = this.getUpgrades(id, clazz);
        if (upgrades == null) return null;
        return upgrades.getValueAtLevel(level);
    }

    @Nullable
    public <T> Price getPriceAtLevel(String id, Class<? extends Upgrades<T>> clazz, int level) {
        final Upgrades<T> upgrades = this.getUpgrades(id, clazz);
        if (upgrades == null) return null;
        return upgrades.getPriceAtLevel(level);
    }

    public void handleEntry(Consumer<EntryUpgrade> consumer) {
        for (EntryUpgrade entryUpgrade : this.entryUpgrades) {
            consumer.accept(entryUpgrade);
        }
    }

    public Set<EntryUpgrade> getEntryUpgrades() {
        return entryUpgrades;
    }
}
