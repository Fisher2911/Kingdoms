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

package io.github.fisher2911.kingdoms.teleport;

import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import org.jetbrains.annotations.Nullable;

public class TeleportInfo {

    private final User user;
    private final WorldPosition to;
    private int secondsLeft;
    private final WorldPosition startPosition;
    @Nullable
    private final String positionId;

    public TeleportInfo(User user, WorldPosition to, int secondsLeft, WorldPosition startPosition, @Nullable String positionId) {
        this.user = user;
        this.to = to;
        this.secondsLeft = secondsLeft;
        this.startPosition = startPosition;
        this.positionId = positionId;
    }

    public User getUser() {
        return user;
    }

    public WorldPosition getTo() {
        return to;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public WorldPosition getStartPosition() {
        return startPosition;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public String getPositionId() {
        return positionId;
    }

    public void decSeconds() {
        this.secondsLeft--;
    }
}
