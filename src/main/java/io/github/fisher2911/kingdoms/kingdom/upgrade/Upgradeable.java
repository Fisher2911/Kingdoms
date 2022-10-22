package io.github.fisher2911.kingdoms.kingdom.upgrade;

public interface Upgradeable {

    UpgradeHolder getUpgradeHolder();
    Integer getUpgradeLevel(String id);

}
