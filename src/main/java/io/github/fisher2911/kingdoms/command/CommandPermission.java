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

package io.github.fisher2911.kingdoms.command;

public enum CommandPermission {

    CREATE_KINGDOM("kingdoms.create"),
    ADMIN_COMMAND("kingdoms.cmd.admin"),
    RELOAD_COMMAND("kingdoms.cmd.reload"),
    VIEW_OTHER_KINGDOM_INFO("kingdoms.cmd.info.other"),
    VIEW_SELF_KINGDOM_INFO("kingdoms.cmd.info.self"),
    VIEW_OTHER_KINGDOM_DESCRIPTION("kingdoms.cmd.description.other"),
    VIEW_SELF_KINGDOM_DESCRIPTION("kingdoms.cmd.description.self"),
    VIEW_ADMIN_COMMAND_HELP("kingdoms.cmd.help.admin"),


    ;

    private final String value;

    CommandPermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
