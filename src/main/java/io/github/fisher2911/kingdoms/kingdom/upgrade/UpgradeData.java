package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;

public class UpgradeData<T> {

    private final Price price;
    private final T value;
    private final String displayValue;

    public UpgradeData(Price price, T value, String displayValue) {
        this.price = price;
        this.value = value;
        this.displayValue = displayValue;
    }

    public Price getPrice() {
        return price;
    }

    public T getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
