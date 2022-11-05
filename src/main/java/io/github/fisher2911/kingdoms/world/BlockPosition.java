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

package io.github.fisher2911.kingdoms.world;

import javax.xml.stream.Location;

public record BlockPosition(int x, int y, int z) {

    BlockPosition fromLocation(Location location) {
        return new BlockPosition(x, y, z);
    }

    public BlockPosition add(int x, int y, int z) {
        return new BlockPosition(this.x + x, this.y + y, this.z + z);
    }

    public BlockPosition subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    public BlockPosition multiply(int x, int y, int z) {
        return new BlockPosition(this.x * x, this.y * y, this.z * z);
    }

    public BlockPosition multiply(int scalar) {
        return this.multiply(scalar, scalar, scalar);
    }

    public BlockPosition divide(int scalar) {
        return this.divide(scalar, scalar, scalar);
    }

    public BlockPosition divide(int x, int y, int z) {
        return new BlockPosition(this.x / x, this.y / y, this.z / z);
    }

}
