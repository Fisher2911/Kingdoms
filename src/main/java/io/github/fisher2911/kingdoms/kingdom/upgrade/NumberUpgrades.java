package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.config.serializer.ItemSerializer;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
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
    protected final BaseItemBuilder displayItem;
    protected final BaseItemBuilder maxLevelDisplayItem;

    public NumberUpgrades(String id, String displayName, Expression valueExpression, Expression moneyPriceExpression, int maxLevel, BaseItemBuilder displayItem, BaseItemBuilder maxLevelDisplayItem) {
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
    public BaseItemBuilder getGuiItem() {
        return this.displayItem;
    }

    @Override
    public BaseItemBuilder getMaxLevelGuiItem() {
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

    public static final String DOUBLE_UPGRADE_TYPE = "double";
    public static final String INT_UPGRADE_TYPE = "int";

    private static final String UPGRADES_TYPE = "type";
    private static final String ID = "id";
    private static final String DISPLAY_NAME = "display-name";
    private static final String EXPRESSION = "expression";
    private static final String MONEY_PRICE_EXPRESSION = "money-price-expression";
    private static final String MAX_LEVEL = "max-level";
    private static final String DISPLAY_ITEM = "display-item";
    private static final String MAX_LEVEL_DISPLAY_ITEM = "max-level-item";

    public static NumberUpgrades<?> deserialize(ConfigurationNode node, String type) {
        try {
            final String id = node.node(ID).getString();
            System.out.println("Loaded upgrade: " + id);
            final String displayName = node.node(DISPLAY_NAME).getString();
            final Expression expression = new ExpressionBuilder(node.node(EXPRESSION).getString("")).variable(CURRENT_LEVEL_VARIABLE).build();
            final Expression moneyPriceExpression = new ExpressionBuilder(node.node(MONEY_PRICE_EXPRESSION).getString("")).variable(CURRENT_LEVEL_VARIABLE).build();
            final int maxLevel = node.node(MAX_LEVEL).getInt();
            final BaseItemBuilder displayItem = ItemSerializer.INSTANCE.deserialize(BaseItemBuilder.class, node.node(DISPLAY_ITEM));
            final BaseItemBuilder maxLevelDisplayItem = ItemSerializer.INSTANCE.deserialize(BaseItemBuilder.class, node.node(MAX_LEVEL_DISPLAY_ITEM));
            return switch (type.toLowerCase()) {
                case DOUBLE_UPGRADE_TYPE -> new DoubleUpgrades(id, displayName, expression, moneyPriceExpression, maxLevel, displayItem, maxLevelDisplayItem);
                case INT_UPGRADE_TYPE -> new IntUpgrades(id, displayName, expression, moneyPriceExpression, maxLevel, displayItem, maxLevelDisplayItem);
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
