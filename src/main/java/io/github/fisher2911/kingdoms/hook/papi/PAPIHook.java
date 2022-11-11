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

package io.github.fisher2911.kingdoms.hook.papi;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.hook.Hook;
import io.github.fisher2911.kingdoms.hook.HookType;

import java.util.List;

public class PAPIHook extends Hook {

    private static final String ID = "PlaceholderAPI";

    private final Kingdoms plugin;

    public PAPIHook(Kingdoms plugin) {
        super(ID, List.of(HookType.PAPI));
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        new KingdomsExpansion(this.plugin).register();
    }

}
