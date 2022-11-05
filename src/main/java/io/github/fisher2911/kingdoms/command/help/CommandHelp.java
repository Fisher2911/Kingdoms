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

package io.github.fisher2911.kingdoms.command.help;

import org.jetbrains.annotations.Nullable;

public class CommandHelp {

    private final String command;
    private final String usage;
    @Nullable
    private final String permission;

    public CommandHelp(String command, String usage, @Nullable String permission) {
        this.command = command;
        this.usage = usage;
        this.permission = permission;
    }

    public String getCommand() {
        return this.command;
    }

    public String getUsage() {
        return this.usage;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "CommandHelp{" +
                "command='" + command + '\'' +
                ", usage='" + usage + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}
