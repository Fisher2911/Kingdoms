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

package io.github.fisher2911.kingdoms.kingdom.upgrade;

import net.objecthunter.exp4j.Expression;
import org.jetbrains.annotations.Nullable;

public class DoubleUpgrades extends NumberUpgrades<Double> {

    public DoubleUpgrades(String id, String displayName, Expression expression, Expression moneyPriceExpression, int maxLevel) {
        super(id, displayName, expression, moneyPriceExpression, maxLevel);
    }

    @Override
    @Nullable
    public Double getValueAtLevel(int level) {
        if (level > this.maxLevel) return null;
        return this.setVariable(level).evaluate();
    }

}
