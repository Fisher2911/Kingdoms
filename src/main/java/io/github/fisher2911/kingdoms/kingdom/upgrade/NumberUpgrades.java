package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.Nullable;

public abstract class NumberUpgrades<T extends Number> implements Upgrades<T> {

    public static final String CURRENT_LEVEL_VARIABLE = "c";

    protected final String id;
    protected final String displayName;
    protected final Expression expression;
    protected final Expression moneyPriceExpression;
    protected final int maxLevel;
    protected final ItemBuilder displayItem;
    protected final ItemBuilder maxLevelDisplayItem;

    public NumberUpgrades(String id, String displayName, Expression valueExpression, Expression moneyPriceExpression, int maxLevel, ItemBuilder displayItem, ItemBuilder maxLevelDisplayItem) {
        this.id = id;
        this.displayName = displayName;
        this.expression = valueExpression;
        this.moneyPriceExpression = moneyPriceExpression;
        this.maxLevel = maxLevel;
        this.displayItem = displayItem;
        this.maxLevelDisplayItem = maxLevelDisplayItem;
    }

    protected Expression setVariable(int level) {
        return this.expression.setVariable(CURRENT_LEVEL_VARIABLE, level);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Nullable
    public Price getPriceAtLevel(int level) {
        if (level > this.maxLevel) return null;
        return Price.money(this.moneyPriceExpression.setVariable(CURRENT_LEVEL_VARIABLE, level).evaluate());
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public ItemBuilder getGuiItem() {
        return this.displayItem;
    }

    @Override
    public ItemBuilder getMaxLevelGuiItem() {
        return this.maxLevelDisplayItem;
    }

    @Override
    public String getDisplayValueAtLevel(int level) {
        return String.valueOf(this.getValueAtLevel(level));
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }
}
