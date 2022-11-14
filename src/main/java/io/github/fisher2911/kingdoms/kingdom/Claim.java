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

package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.api.metadata.MetadataKey;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermissible;
import io.github.fisher2911.fisherlib.world.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Claim extends KPermissible {

    boolean isWilderness();
    int getKingdomId();
    @Nullable
    <T> T getData(MetadataKey key, Class<T> clazz);
    void setData(MetadataKey key, Object value);
    UUID getWorld();
    ChunkPos getChunk();

}
