package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.HashMap;
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
            for (final var entry : upgradesNode.childrenMap().entrySet()) {
                final var upgrade = this.load(entry.getValue());
                if (upgrade == null) continue;
                upgrades.put(upgrade.getId(), upgrade);
            }
            this.upgradeHolder = new UpgradeHolder(upgrades);
        } catch (IOException e) {
            throw new RuntimeException("Could not load upgrades", e);
        }
//        final Map<String, Upgrades<?>> upgrades = new HashMap<>();
//        final Expression valueExpression = new ExpressionBuilder("1 * c").variable(NumberUpgrades.CURRENT_LEVEL_VARIABLE).build();
//        final Expression priceExpression = new ExpressionBuilder("100 * c").variable(NumberUpgrades.CURRENT_LEVEL_VARIABLE).build();
//        upgrades.put(UpgradeId.MAX_CLAIMS.toString(), new IntUpgrades(
//                UpgradeId.MAX_CLAIMS.toString(),
//                UpgradeId.MAX_CLAIMS.displayName(),
//                valueExpression,
//                priceExpression,
//                10,
//                ItemBuilder.from(Material.GRASS_BLOCK).
//                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
//                        lore(List.of(
//                                "",
//                                "<green>" + Placeholder.UPGRADE_DISPLAY_VALUE,
//                                "<red>$" + Placeholder.UPGRADE_DISPLAY_PRICE,
//                                "<gray>Max Claims: " + Placeholder.UPGRADE_DISPLAY_VALUE
//                        )),
//                ItemBuilder.from(Material.BARRIER).
//                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
//                        lore(List.of(
//                                "",
//                                "<gray>Max Claims: " + Placeholder.UPGRADE_DISPLAY_VALUE,
//                                "<red>Max Level"
//                        ))
//        ));
//
//        upgrades.put(UpgradeId.MAX_MEMBERS.toString(), new IntUpgrades(
//                UpgradeId.MAX_MEMBERS.toString(),
//                UpgradeId.MAX_MEMBERS.displayName(),
//                valueExpression,
//                priceExpression,
//                2,
//                ItemBuilder.from(Material.IRON_BARS).
//                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
//                        lore(List.of(
//                                "",
//                                "<green>" + Placeholder.UPGRADE_DISPLAY_VALUE,
//                                "<red>$" + Placeholder.UPGRADE_DISPLAY_PRICE,
//                                "<gray>Members: " + Placeholder.UPGRADE_DISPLAY_VALUE
//                        )),
//                ItemBuilder.from(Material.BARRIER).
//                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
//                        lore(List.of(
//                                "",
//                                "<gray>Members: " + Placeholder.UPGRADE_DISPLAY_VALUE,
//                                "<red>Max Level"
//                        ))
//        ));
//
//        final Expression bankValueExpression = new ExpressionBuilder("200 * c").variable(NumberUpgrades.CURRENT_LEVEL_VARIABLE).build();
//        final Expression bankPriceExpression = new ExpressionBuilder("200 * c").variable(NumberUpgrades.CURRENT_LEVEL_VARIABLE).build();
//
//        upgrades.put(UpgradeId.BANK_LIMIT.toString(), new DoubleUpgrades(
//                UpgradeId.BANK_LIMIT.toString(),
//                UpgradeId.BANK_LIMIT.displayName(),
//                bankValueExpression,
//                bankPriceExpression,
//                6,
//                ItemBuilder.from(Material.EMERALD_BLOCK).
//                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
//                        lore(List.of(
//                                "",
//                                "<green>" + Placeholder.UPGRADE_DISPLAY_VALUE,
//                                "<red> $" + Placeholder.UPGRADE_DISPLAY_PRICE,
//                                "<gray>Bank Limit: " + Placeholder.UPGRADE_DISPLAY_VALUE
//                        )),
//                ItemBuilder.from(Material.BARRIER).
//                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
//                        lore(List.of(
//                                "",
//                                "<gray>Bank limit: " + Placeholder.UPGRADE_DISPLAY_VALUE,
//                                "<red>Max Level"
//                        ))
//        ));
    }
}
