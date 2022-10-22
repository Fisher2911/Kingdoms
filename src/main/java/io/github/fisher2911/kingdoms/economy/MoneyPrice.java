package io.github.fisher2911.kingdoms.economy;

import io.github.fisher2911.kingdoms.user.User;

public class MoneyPrice implements Price {

    private final double price;

    public MoneyPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean canAfford(User user) {
        return user.getMoney() >= this.price;
    }

    @Override
    public void pay(User user) {
        user.takeMoney(this.price);
    }

    @Override
    public boolean payIfCanAfford(User user) {
        if (!this.canAfford(user)) return false;
        user.takeMoney(this.price);
        return true;
    }

    @Override
    public String getDisplay() {
        return String.valueOf(this.price);
    }
}
