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

package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.util.Metadata;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class SimpleConditionalItem extends ConditionalItem {

    private final BaseGuiItem item;

    public SimpleConditionalItem(BaseGuiItem item, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        this(item, Metadata.empty(), placeholders);
    }

    public SimpleConditionalItem(BaseGuiItem item, Metadata metadata, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        super(metadata, Collections.emptyList(), placeholders);
        this.item = item;
    }

    public SimpleConditionalItem(BaseGuiItem item) {
        this(item, Collections.emptyList());
    }

    @Override
    public BaseGuiItem getItem(Metadata metadata) {
        return this.item.withMetaData(metadata, false).withPlaceholders(this.placeholders);
    }

    public static class Builder extends ConditionalItem.Builder {

        private final BaseGuiItem item;

        public Builder(BaseGuiItem item) {
            super();
            this.item = item;
        }

        public Builder(SimpleConditionalItem item) {
            super(item);
            this.item = item.item.copy();
        }

        public Builder(ConditionalItem item, BaseGuiItem item1) {
            super(item);
            this.item = item1.copy();
        }

        public ConditionalItem build() {
            return new SimpleConditionalItem(this.item, this.metadata, this.placeholders);
        }

    }
}
