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

import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KingdomMemberStartTeleportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    private final User user;
    @NotNull
    private WorldPosition to;
    @NotNull
    private final String positionId;
    private int delay;
    private boolean cancelled;

    public KingdomMemberStartTeleportEvent(@NotNull User user, @NotNull WorldPosition to, @NotNull String positionId, int delay) {
        super(!Bukkit.isPrimaryThread());
        this.user = user;
        this.to = to;
        this.positionId = positionId;
        this.delay = delay;
    }

    public KingdomMemberStartTeleportEvent(boolean isAsync, @NotNull User user, @NotNull WorldPosition to, @NotNull String positionId, int delay) {
        super(isAsync);
        this.user = user;
        this.to = to;
        this.positionId = positionId;
        this.delay = delay;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    @NotNull
    public WorldPosition getTo() {
        return to;
    }

    public void setTo(@NotNull WorldPosition to) {
        this.to = to;
    }

    @NotNull
    public String getPositionId() {
        return positionId;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
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
