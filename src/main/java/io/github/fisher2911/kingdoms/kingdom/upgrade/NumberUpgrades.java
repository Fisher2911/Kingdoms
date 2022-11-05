package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;

public abstract class NumberUpgrades<T extends Number> implements Upgrades<T> {

    public static final String CURRENT_LEVEL_VARIABLE = "level";

    protected final String id;
    protected final String displayName;
    protected final Expression expression;
    protected final Expression moneyPriceExpression;
    protected final int maxLevel;

    public NumberUpgrades(String id, String displayName, Expression valueExpression, Expression moneyPriceExpression, int maxLevel) {
        this.id = id;
        this.displayName = displayName;
        this.expression = valueExpression;
        this.moneyPriceExpression = moneyPriceExpression;
        this.maxLevel = maxLevel;
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
    public String getDisplayValueAtLevel(int level) {
        return String.valueOf(this.getValueAtLevel(level));
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    public static final String DOUBLE_UPGRADE_TYPE = "double";
    public static final String INT_UPGRADE_TYPE = "int";

    private static final String UPGRADES_TYPE = "type";
    private static final String ID = "id";
    private static final String DISPLAY_NAME = "display-name";
    private static final String EXPRESSION = "expression";
    private static final String MONEY_PRICE_EXPRESSION = "money-price-expression";
    private static final String MAX_LEVEL = "max-level";

    public static NumberUpgrades<?> deserialize(ConfigurationNode node, String type) {
        try {
            final String id = node.node(ID).getString();
            final String displayName = node.node(DISPLAY_NAME).getString();
            final Expression expression = new ExpressionBuilder(node.node(EXPRESSION).getString("")).variable(CURRENT_LEVEL_VARIABLE).build();
            final Expression moneyPriceExpression = new ExpressionBuilder(node.node(MONEY_PRICE_EXPRESSION).getString("")).variable(CURRENT_LEVEL_VARIABLE).build();
            final int maxLevel = node.node(MAX_LEVEL).getInt();
            return switch (type.toLowerCase()) {
                case DOUBLE_UPGRADE_TYPE -> new DoubleUpgrades(id, displayName, expression, moneyPriceExpression, maxLevel);
                case INT_UPGRADE_TYPE -> new IntUpgrades(id, displayName, expression, moneyPriceExpression, maxLevel);
                default -> throw new SerializationException("Invalid upgrades type: " + type);
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize number upgrades", e);
        }
    }

    public static NumberUpgrades<?> deserialize(ConfigurationNode node) {
        return deserialize(node, node.node(UPGRADES_TYPE).getString(""));
    }

    public static IntUpgrades deserializeIntUpgrades(ConfigurationNode node) {
        return (IntUpgrades) deserialize(node, INT_UPGRADE_TYPE);
    }

    public static DoubleUpgrades deserializeDoubleUpgrades(ConfigurationNode node) {
        return (DoubleUpgrades) deserialize(node, DOUBLE_UPGRADE_TYPE);
    }

}
