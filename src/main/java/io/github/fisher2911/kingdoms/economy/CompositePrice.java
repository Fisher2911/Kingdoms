/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    @Override
    public String getDisplay() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (Price price : this.prices) {
            builder.append(price.getDisplay());
            if (index < this.prices.size() - 1) builder.append("\n");
            index++;
        }
        return builder.toString();
    }
}
