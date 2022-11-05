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

package io.github.fisher2911.kingdoms.api.event.kingdom;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KingdomUnloadEvent extends KingdomEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public KingdomUnloadEvent(@NotNull Kingdom kingdom) {
        super(kingdom);
    }

    public KingdomUnloadEvent(@NotNull Kingdom kingdom, boolean isAsync) {
        super(kingdom, isAsync);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
