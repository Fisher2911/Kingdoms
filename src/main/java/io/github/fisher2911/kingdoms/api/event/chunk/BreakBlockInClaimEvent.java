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

package io.github.fisher2911.kingdoms.api.event.chunk;

import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BreakBlockInClaimEvent extends ClaimedChunkEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    private final BlockBreakEvent event;
    @NotNull
    private final User user;
    private boolean cancelled;

    public BreakBlockInClaimEvent(@NotNull Kingdom kingdom, @NotNull ClaimedChunk chunk, @NotNull BlockBreakEvent event, @NotNull User user) {
        super(kingdom, chunk);
        this.event = event;
        this.user = user;
    }

    public BreakBlockInClaimEvent(@NotNull Kingdom kingdom, boolean isAsync, @NotNull ClaimedChunk chunk, @NotNull BlockBreakEvent event, @NotNull User user) {
        super(kingdom, isAsync, chunk);
        this.event = event;
        this.user = user;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    @NotNull
    public BlockBreakEvent getEvent() {
        return event;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
