package io.github.fisher2911.kingdoms.kingdom.upgrade;

import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.Nullable;

public class DoubleUpgrades extends NumberUpgrades<Double> {

    public DoubleUpgrades(String id, String displayName, Expression expression, Expression moneyPriceExpression, int maxLevel) {
        super(id, displayName, expression, moneyPriceExpression, maxLevel);
    }

    @Override
    @Nullable
    public Double getValueAtLevel(int level) {
        if (level > this.maxLevel) return null;
        return this.setVariable(level).evaluate();
    }

}
