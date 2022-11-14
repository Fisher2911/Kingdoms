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

package io.github.fisher2911.kingdoms.config.condition;

import io.github.fisher2911.fisherlib.config.condition.MetadataPredicate;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.fisherlib.util.Metadata;

import java.util.List;

public class ItemConditions implements ItemConditional {

    private final List<MetadataPredicate> conditionalList;
    private final ConditionalItem item;

    public static ItemConditions alwaysTrue(ConditionalItem item) {
        return new ItemConditions(List.of(), item);
    }

    public ItemConditions(List<MetadataPredicate> conditionalList, ConditionalItem item) {
        this.conditionalList = conditionalList;
        this.item = item;
    }

    public boolean test(Metadata possible) {
        for (final MetadataPredicate conditional : this.conditionalList) {
            if (!conditional.test(possible)) {
                return false;
            }
        }
        return true;
    }

    public List<MetadataPredicate> getConditionalList() {
        return conditionalList;
    }

    public ConditionalItem getItem() {
        return item;
    }
}
