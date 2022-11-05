package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class UpgradeManager extends Config {


    public UpgradeManager(Kingdoms plugin) {
        super(plugin, "kingdom-defaults", "upgrades.yml");
    }

    private UpgradeHolder upgradeHolder;

    public UpgradeHolder getUpgradeHolder() {
        return upgradeHolder;
    }

    private static final String UPGRADE_TYPE = "type";
    private static final Map<String, Function<ConfigurationNode, Upgrades<?>>> typeUpgradeLoaders = new HashMap<>();

    static {
        typeUpgradeLoaders.put(NumberUpgrades.DOUBLE_UPGRADE_TYPE, NumberUpgrades::deserialize);
        typeUpgradeLoaders.put(NumberUpgrades.INT_UPGRADE_TYPE, NumberUpgrades::deserialize);
        typeUpgradeLoaders.put(PotionUpgrades.POTION_UPGRADE_TYPE, PotionUpgrades::deserialize);
    }

    @Nullable
    public Upgrades<?> load(ConfigurationNode node) {
        final String type = node.node(UPGRADE_TYPE).getString("");
        final var function = typeUpgradeLoaders.get(type);
        if (function == null) return null;
        return function.apply(node);
    }

    private static final String UPGRADES = "upgrades";

    public void reload() {
        this.load();
    }

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.
                builder().
                path(this.path).
                build();
        try {
            final var source = loader.load();
            final var upgradesNode = source.node(UPGRADES);
            final Map<String, Upgrades<?>> upgrades = new HashMap<>();
            final List<String> upgradeIdOrder = new ArrayList<>();
            for (final var entry : upgradesNode.childrenMap().entrySet()) {
                final var upgrade = this.load(entry.getValue());
                if (upgrade == null) continue;
                upgradeIdOrder.add(upgrade.getId());
                upgrades.put(upgrade.getId(), upgrade);
            }
            this.upgradeHolder = new UpgradeHolder(upgrades, upgradeIdOrder);
        } catch (IOException e) {
            throw new RuntimeException("Could not load upgrades", e);
        }
    }
}
