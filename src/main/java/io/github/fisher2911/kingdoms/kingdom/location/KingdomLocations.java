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

package io.github.fisher2911.kingdoms.kingdom.location;

import io.github.fisher2911.kingdoms.data.Saveable;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class KingdomLocations implements Saveable {

    public static final String HOME = "home";

    private final Map<String, WorldPosition> savedPositions;
    private boolean dirty;

    public KingdomLocations(final Map<String, WorldPosition> savedPositions) {
        this.savedPositions = savedPositions;
    }

    public Map<String, WorldPosition> getSavedPositions() {
        return savedPositions;
    }

    @Nullable
    public WorldPosition getPosition(String id) {
        return this.savedPositions.get(id);
    }

    public WorldPosition setPosition(String id, WorldPosition position) {
        this.dirty = true;
        return this.savedPositions.put(id, position);
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
