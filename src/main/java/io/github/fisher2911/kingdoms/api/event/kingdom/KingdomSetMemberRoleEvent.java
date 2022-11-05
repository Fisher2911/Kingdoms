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
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KingdomSetMemberRoleEvent extends KingdomEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    private final User setter;
    @NotNull
    private final User toSet;
    @NotNull
    private Role role;
    private boolean cancelled;

    public KingdomSetMemberRoleEvent(@NotNull Kingdom kingdom, @NotNull User setter, @NotNull User toSet, @NotNull Role role) {
        super(kingdom);
        this.setter = setter;
        this.toSet = toSet;
        this.role = role;
    }

    public KingdomSetMemberRoleEvent(@NotNull Kingdom kingdom, boolean isAsync, @NotNull User setter, @NotNull User toSet, @NotNull Role role) {
        super(kingdom, isAsync);
        this.setter = setter;
        this.toSet = toSet;
        this.role = role;
    }

    @NotNull
    public User getSetter() {
        return setter;
    }

    @NotNull
    public User getToSet() {
        return toSet;
    }

    @NotNull
    public Role getRole() {
        return role;
    }

    public void setRole(@NotNull Role role) {
        this.role = role;
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
