package io.github.fisher2911.kingdoms.kingdom.upgrade;

public class UpgradeLevel<T> {

    private final UpgradeData<T> upgrade;
    private final int level;

    public UpgradeLevel(UpgradeData<T> upgrade, int level) {
        this.upgrade = upgrade;
        this.level = level;
    }

    public UpgradeData<T> getUpgrade() {
        return upgrade;
    }

    public int getLevel() {
        return level;
    }
}
