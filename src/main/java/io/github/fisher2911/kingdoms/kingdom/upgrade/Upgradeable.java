package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import org.jetbrains.annotations.Nullable;

public interface Upgradeable {

    UpgradeHolder getUpgradeHolder();

    void setUpgradeLevel(String id, int level);

    @Nullable
    Integer getUpgradeLevel(String id);

    Price getUpgradePrice(String id);

    <T> T getUpgradesValue(String id, Class<? extends Upgrades<T>> clazz);

}
