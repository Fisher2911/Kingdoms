package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.Config;
import io.github.fisher2911.kingdoms.placeholder.Placeholder;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeManager extends Config {

    public UpgradeManager(Kingdoms plugin) {
        super(plugin, "upgrades");
    }

    private UpgradeHolder upgradeHolder;

    public UpgradeHolder getUpgradeHolder() {
        return upgradeHolder;
    }

    public void load() {
        final Map<String, Upgrades<?>> upgrades = new HashMap<>();
        final Expression valueExpression = new ExpressionBuilder("1 * c").variable(NumberUpgrades.CURRENT_LEVEL_VARIABLE).build();
        final Expression priceExpression = new ExpressionBuilder("100 * c").variable(NumberUpgrades.CURRENT_LEVEL_VARIABLE).build();
        upgrades.put(UpgradeId.MAX_CLAIMS.toString(), new IntUpgrades(
                UpgradeId.MAX_CLAIMS.toString(),
                UpgradeId.MAX_CLAIMS.displayName(),
                valueExpression,
                priceExpression,
                10,
                ItemBuilder.from(Material.GRASS_BLOCK).
                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME).
                        lore(List.of(
                                "",
                                "<green>" + Placeholder.UPGRADE_DISPLAY_VALUE,
                                "<red>" + Placeholder.UPGRADE_DISPLAY_PRICE,
                                "<gray>Max Claims: " + Placeholder.UPGRADE_DISPLAY_VALUE
                        )),
                ItemBuilder.from(Material.BARRIER).
                        name("<blue>" +  Placeholder.UPGRADE_DISPLAY_NAME.toString()).
                        lore(List.of(
                                "",
                                "<gray>Max Claims: " + Placeholder.UPGRADE_DISPLAY_VALUE,
                                "<red>Max Level"
                        ))
        ));

        upgrades.put(UpgradeId.MAX_MEMBERS.toString(), new IntUpgrades(
                UpgradeId.MAX_MEMBERS.toString(),
                UpgradeId.MAX_MEMBERS.displayName(),
                valueExpression,
                priceExpression,
                2,
                ItemBuilder.from(Material.IRON_BARS).
                        name("<blue>" + Placeholder.UPGRADE_DISPLAY_NAME.toString()).
                        lore(List.of(
                                "",
                                "<green>" + Placeholder.UPGRADE_DISPLAY_VALUE,
                                "<red>" + Placeholder.UPGRADE_DISPLAY_PRICE,
                                "<gray>Members: " + Placeholder.UPGRADE_DISPLAY_VALUE
                        )),
                ItemBuilder.from(Material.BARRIER).
                        name("<blue>" +  Placeholder.UPGRADE_DISPLAY_NAME.toString()).
                        lore(List.of(
                                "",
                                "<gray>Members: " + Placeholder.UPGRADE_DISPLAY_VALUE,
                                "<red>Max Level"
                        ))
        ));
        this.upgradeHolder = new UpgradeHolder(upgrades);
    }
}
