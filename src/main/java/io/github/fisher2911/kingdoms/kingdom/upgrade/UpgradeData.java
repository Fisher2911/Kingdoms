package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.economy.Price;

public class UpgradeData<T> {

    private final Price price;
    private final T value;

    public UpgradeData(Price price, T value) {
        this.price = price;
        this.value = value;
    }

    public Price getPrice() {
        return price;
    }

    public T getValue() {
        return value;
    }
}
