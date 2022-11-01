package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.Nullable;

public class IntUpgrades extends NumberUpgrades<Integer> {

    public IntUpgrades(String id, String displayName, Expression expression, Expression moneyPriceExpression, int maxLevel, BaseItemBuilder displayItem, BaseItemBuilder maxLevelDisplayItem) {
        super(id, displayName, expression, moneyPriceExpression, maxLevel, displayItem, maxLevelDisplayItem);
    }

    @Override
    @Nullable
    public Integer getValueAtLevel(int level) {
        if (level > this.maxLevel) return null;
        return (int) this.setVariable(level).evaluate();
    }

}
