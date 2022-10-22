package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import net.objecthunter.exp4j.Expression;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class PotionUpgrades extends IntUpgrades implements EntryUpgrade<Integer> {

    private final Set<PotionEffectType> potionEffectTypes;

    public PotionUpgrades(String id, String displayName, Expression expression, Expression priceExpression, int maxLevel, ItemBuilder displayItem, ItemBuilder maxLevelDisplayItem, Set<PotionEffectType> potionEffectTypes) {
        super(id, displayName, expression, priceExpression, maxLevel, displayItem, maxLevelDisplayItem);
        this.potionEffectTypes = potionEffectTypes;
    }

    public Set<PotionEffectType> getPotionEffectTypes() {
        return potionEffectTypes;
    }

    @Override
    public void onEnter(User user, int level) {
        if (!user.isOnline()) return;
        final Player player = user.getPlayer();
        final Integer potionLevel = this.getValueAtLevel(level);
        if (potionLevel == null) return;
        for (PotionEffectType type : this.potionEffectTypes) {
            player.addPotionEffect(type.createEffect(Integer.MAX_VALUE, potionLevel));
        }
    }

    @Override
    public void onLeave(User user, int level) {
        if (!user.isOnline()) return;
        final Player player = user.getPlayer();
        for (PotionEffectType type : this.potionEffectTypes) {
            player.removePotionEffect(type);
        }
    }
}
