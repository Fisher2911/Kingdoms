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

package io.github.fisher2911.kingdoms.api.event.user;

import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class UserEvent extends Event {

    @NotNull
    private final User user;

    public UserEvent(@NotNull User user) {
        super(!Bukkit.isPrimaryThread());
        this.user = user;
    }

    public UserEvent(boolean isAsync, @NotNull User user) {
        super(isAsync);
        this.user = user;
    }

    @NotNull
    public User getUser() {
        return user;
    }

}
