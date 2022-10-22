package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class NumberUpgrades<T extends Number> implements Upgrades<T>  {

    private static final String CURRENT_LEVEL_VARIABLE = "c";

    protected final String id;
    protected final int maxLevel;
    protected final Map<Integer, Price> prices;

    protected final Expression expression;

    public NumberUpgrades(String id, Expression expression, int maxLevel, Map<Integer, Price> prices) {
        this.id = id;
        this.expression = expression;
        this.maxLevel = maxLevel;
        this.prices = prices;
    }

    protected Expression setVariable(int level) {
        return this.expression.setVariable(CURRENT_LEVEL_VARIABLE, level);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public @Nullable T getValueAtLevel(int level) {
        return null;
    }

    @Override
    @Nullable
    public Price getPriceAtLevel(int level) {
        if (level > this.maxLevel) return null;
        return this.prices.get(level);
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }
}
