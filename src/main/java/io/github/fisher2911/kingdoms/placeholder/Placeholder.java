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

package io.github.fisher2911.kingdoms.placeholder;

public enum Placeholder {

    USER_NAME,
    USER_BALANCE,
    USER_KINGDOM_NAME,

    KINGDOM_ID,
    KINGDOM_NAME,
    KINGDOM_MEMBERS,
    KINGDOM_DESCRIPTION,
    KINGDOM_ALLIES,
    KINGDOM_TRUCES,
    KINGDOM_ENEMIES,

    PERMISSION_NAME,
    PERMISSION_DISPLAY_NAME,
    PERMISSION_VALUE,
    PERMISSION_DISPLAY_VALUE,

    UPGRADE_ID,
    UPGRADE_DISPLAY_NAME,
    UPGRADE_VALUE,
    UPGRADE_DISPLAY_VALUE,
    UPGRADE_DISPLAY_PRICE,
    UPGRADE_LEVEL,

    CHUNK_X,
    CHUNK_Z,

    CHAT_CHANNEL,

    ROLE_DISPLAY_NAME,
    ROLE_WEIGHT,
    ROLE_ID,

    RELATION_DISPLAY_NAME,

    TRANSACTION_AMOUNT,

    KINGDOM_BANK_BALANCE,

    POSITION_X,
    POSITION_Y,
    POSITION_Z,

    // kingdom + user placeholders
    KINGDOM_MEMBER_ROLE_DISPLAY_NAME,
    KINGDOM_MEMBER_ROLE_ID,
    KINGDOM_MEMBER_ROLE_WEIGHT,
    KINGDOM_MEMBER_UUID,
    KINGDOM_MEMBER_NAME,

    TELEPORT_INFO_SECONDS_LEFT,

    GUI_USER_ROLE_ID,
    GUI_USER_ROLE_WEIGHT,
    GUI_USER_UUID,
    GUI_KINGDOM_ID,
    GUI_KINGDOM_NAME,

    COMMAND_HELP_NAME,
    COMMAND_HELP_USAGE,
    COMMAND_HELP_PERMISSION,

    COMMAND_INFO_PREVIOUS_PAGE_NUMBER,
    COMMAND_INFO_NEXT_PAGE_NUMBER,


    ;

    public String toString() {
        return "%" + super.toString().toLowerCase() + "%";
    }


}
