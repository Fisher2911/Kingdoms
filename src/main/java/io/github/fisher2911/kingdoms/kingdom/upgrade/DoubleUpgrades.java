package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DoubleUpgrades extends NumberUpgrades<Double> {

    public DoubleUpgrades(String id, Expression expression, int maxLevel, Map<Integer, Price> prices) {
        super(id, expression, maxLevel, prices);
    }

    @Override
    @Nullable
    public Double getValueAtLevel(int level) {
        if (level > this.maxLevel) return null;
        return this.setVariable(level).evaluate();
    }

}
