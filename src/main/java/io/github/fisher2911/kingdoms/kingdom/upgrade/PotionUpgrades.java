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

import io.github.fisher2911.fisherlib.configurate.ConfigurationNode;
import io.github.fisher2911.fisherlib.exp4j.Expression;
import io.github.fisher2911.fisherlib.upgrade.IntUpgrades;
import io.github.fisher2911.fisherlib.upgrade.NumberUpgrades;
import io.github.fisher2911.fisherlib.util.EnumUtil;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class PotionUpgrades extends IntUpgrades implements KingdomEntryUpgrades<Integer> {

    private final Set<PotionEffectType> potionEffectTypes;
    private final Set<RelationType> appliesTo;
    private final boolean appliesToSelf;

    public PotionUpgrades(
            String id,
            String displayName,
            Expression expression,
            Expression priceExpression,
            int maxLevel,
            Set<PotionEffectType> potionEffectTypes,
            Set<RelationType> appliesTo,
            boolean appliesToSelf
    ) {
        super(id, displayName, expression, priceExpression, maxLevel);
        this.potionEffectTypes = potionEffectTypes;
        this.appliesTo = appliesTo;
        this.appliesToSelf = appliesToSelf;
    }

    public PotionUpgrades(IntUpgrades upgrades, Set<PotionEffectType> potionEffectTypes, Set<RelationType> appliesTo, boolean appliesToSelf) {
        super(upgrades.getId(), upgrades.getDisplayName(), upgrades.getExpression(), upgrades.getMoneyPriceExpression(), upgrades.getMaxLevel());
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

    public Set<RelationType> appliesTo() {
        return this.appliesTo;
    }

    @Override
    public boolean appliesTo(Kingdom kingdom, User user) {
        return this.appliesTo.contains(kingdom.getKingdomRelation(user.getKingdomId()));
    }

    @Override
    public boolean appliesTo(RelationType relationType) {
        return false;
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
