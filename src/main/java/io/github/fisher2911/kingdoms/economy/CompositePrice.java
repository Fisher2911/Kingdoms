package io.github.fisher2911.kingdoms.economy;

import io.github.fisher2911.kingdoms.user.User;

import java.util.Collection;
import java.util.List;

public class CompositePrice implements Price {

    private final Collection<Price> prices;

    private CompositePrice(Collection<Price> prices) {
        this.prices = prices;
    }

    public static CompositePrice of(Price... prices) {
        return new CompositePrice(List.of(prices));
    }

    @Override
    public boolean canAfford(User user) {
        for (Price price : this.prices) {
            if (!price.canAfford(user)) return false;
        }
        return true;
    }

    @Override
    public void pay(User user) {
        for (Price price : this.prices) {
            price.pay(user);
        }
    }

    @Override
    public boolean payIfCanAfford(User user) {
        if (!canAfford(user)) return false;
        this.pay(user);
        return true;
    }
}
