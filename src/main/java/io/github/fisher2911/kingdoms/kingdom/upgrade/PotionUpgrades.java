package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.EnumUtil;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import net.objecthunter.exp4j.Expression;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class PotionUpgrades extends IntUpgrades implements EntryUpgrade<Integer> {

    private final Set<PotionEffectType> potionEffectTypes;
    private final Set<RelationType> appliesTo;
    private final boolean appliesToSelf;

    public PotionUpgrades(
            String id,
            String displayName,
            Expression expression,
            Expression priceExpression,
            int maxLevel,
            ItemBuilder displayItem,
            ItemBuilder maxLevelDisplayItem,
            Set<PotionEffectType> potionEffectTypes,
            Set<RelationType> appliesTo,
            boolean appliesToSelf
    ) {
        super(id, displayName, expression, priceExpression, maxLevel, displayItem, maxLevelDisplayItem);
        this.potionEffectTypes = potionEffectTypes;
        this.appliesTo = appliesTo;
        this.appliesToSelf = appliesToSelf;
    }

    public PotionUpgrades(IntUpgrades upgrades, Set<PotionEffectType> potionEffectTypes, Set<RelationType> appliesTo, boolean appliesToSelf) {
        super(upgrades.id, upgrades.displayName, upgrades.expression, upgrades.moneyPriceExpression, upgrades.getMaxLevel(), upgrades.displayItem, upgrades.maxLevelDisplayItem);
        this.potionEffectTypes = potionEffectTypes;
        this.appliesTo = appliesTo;
        this.appliesToSelf = appliesToSelf;
    }

    public Set<PotionEffectType> getPotionEffectTypes() {
        return potionEffectTypes;
    }

    @Override
    public void onEnter(Kingdom entering, User user, int level) {
        if (!user.isOnline()) return;
        if (user.getKingdomId() == entering.getId() && !this.appliesToSelf) return;
        if (!this.appliesTo.contains(entering.getKingdomRelation(user.getKingdomId()))) return;
        final Player player = user.getPlayer();
        final Integer potionLevel = this.getValueAtLevel(level);
        if (potionLevel == null) return;
        for (PotionEffectType type : this.potionEffectTypes) {
            player.addPotionEffect(type.createEffect(Integer.MAX_VALUE, potionLevel));
        }
    }

    @Override
    public void onLeave(Kingdom leaving, User user, int level) {
        if (!user.isOnline()) return;
        if (user.getKingdomId() == leaving.getId() && !this.appliesToSelf) return;
        if (!this.appliesTo.contains(leaving.getKingdomRelation(user.getKingdomId()))) return;
        final Player player = user.getPlayer();
        for (PotionEffectType type : this.potionEffectTypes) {
            player.removePotionEffect(type);
        }
    }

    @Override
    public Set<RelationType> appliesTo() {
        return this.appliesTo;
    }

    @Override
    public boolean appliesToSelf() {
        return this.appliesToSelf;
    }

    public static final String POTION_UPGRADE_TYPE = "potion";

    private static final String POTION_EFFECT_TYPES = "potion-effect-types";
    private static final String APPLIES_TO = "applies-to";
    private static final String APPLIES_TO_SELF = "applies-to-self";

    public static PotionUpgrades deserialize(ConfigurationNode node) {
        try {
            final Set<PotionEffectType> types = node.node(POTION_EFFECT_TYPES).getList(String.class, new ArrayList<>()).
                    stream().
                    map(PotionEffectType::getByName).
                    filter(e -> e != null).
                    collect(java.util.stream.Collectors.toSet());
            final Set<RelationType> appliesTo = node.node(APPLIES_TO).getList(String.class, new ArrayList<>()).
                    stream().
                    map(r -> EnumUtil.valueOf(RelationType.class, r)).
                    filter(e -> e != null).
                    collect(Collectors.toSet());
            final boolean appliesToSelf = node.node(APPLIES_TO_SELF).getBoolean(false);
            final IntUpgrades upgrades = NumberUpgrades.deserializeIntUpgrades(node);
            return new PotionUpgrades(upgrades, types, appliesTo, appliesToSelf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize potion upgrades", e);
        }
    }
}
