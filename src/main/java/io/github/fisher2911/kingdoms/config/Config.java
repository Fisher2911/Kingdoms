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

package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;

import java.nio.file.Path;

public abstract class Config {

    protected final Kingdoms plugin;
    protected final Path path;

    public Config(Kingdoms plugin, String... path) {
        this.plugin = plugin;
        if (path.length == 0) throw new IllegalArgumentException("No file path provided");
        final String first = path[0];
        if (path.length == 1) {
            this.path = this.plugin.getDataFolder().toPath().resolve(Path.of(first));
            if (!this.path.toFile().exists()) {
                this.plugin.saveResource(first, false);
            }
            return;
        }
        final String[] theRest = new String[path.length - 1];
        System.arraycopy(path, 1, theRest, 0, theRest.length);
        this.path = this.plugin.getDataFolder().toPath().resolve(Path.of(first, theRest));
        if (!this.path.toFile().exists()) {
            this.plugin.saveResource(Path.of(first, theRest).toString(), false);
        }
    }

}
