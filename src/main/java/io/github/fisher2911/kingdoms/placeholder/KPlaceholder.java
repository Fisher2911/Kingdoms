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

import io.github.fisher2911.fisherlib.placeholder.Placeholder;

import static io.github.fisher2911.fisherlib.placeholder.Placeholder.fromString;

public class KPlaceholder {

    private KPlaceholder() {}

    public static final Placeholder USER_KINGDOM_NAME = fromString("user_kingdom_name");

    public static final Placeholder KINGDOM_ID = fromString("kingdom_id");
    public static final Placeholder KINGDOM_NAME = fromString("kingdom_name");
    public static final Placeholder KINGDOM_MEMBERS = fromString("kingdom_members");
    public static final Placeholder KINGDOM_DESCRIPTION = fromString("kingdom_description");
    public static final Placeholder KINGDOM_ALLIES = fromString("kingdom_allies");
    public static final Placeholder KINGDOM_TRUCES = fromString("kingdom_truces");
    public static final Placeholder KINGDOM_ENEMIES = fromString("kingdom_enemies");
    public static final Placeholder KINGDOM_TOTAL_CLAIMS = fromString("kingdom_total_claims");
    public static final Placeholder PERMISSION_NAME = fromString("permission_name");
    public static final Placeholder PERMISSION_DISPLAY_NAME = fromString("permission_display_name");
    public static final Placeholder PERMISSION_VALUE = fromString("permission_value");
    public static final Placeholder PERMISSION_DISPLAY_VALUE = fromString("permission_display_value");
    public static final Placeholder UPGRADE_ID = fromString("upgrade_id");
    public static final Placeholder UPGRADE_DISPLAY_NAME = fromString("upgrade_display_name");
    public static final Placeholder UPGRADE_VALUE = fromString("upgrade_value");
    public static final Placeholder UPGRADE_DISPLAY_VALUE = fromString("upgrade_display_value");
    public static final Placeholder UPGRADE_DISPLAY_PRICE = fromString("upgrade_display_price");
    public static final Placeholder UPGRADE_LEVEL = fromString("upgrade_level");
    public static final Placeholder CHUNK_X = fromString("chunk_x");
    public static final Placeholder CHUNK_Z = fromString("chunk_z");
    public static final Placeholder CHAT_CHANNEL = fromString("chat_channel");
    public static final Placeholder ROLE_DISPLAY_NAME = fromString("role_display_name");
    public static final Placeholder ROLE_WEIGHT = fromString("role_weight");
    public static final Placeholder ROLE_ID = fromString("role_id");
    public static final Placeholder RELATION_DISPLAY_NAME = fromString("relation_display_name");
    public static final Placeholder TRANSACTION_AMOUNT = fromString("transaction_amount");
    public static final Placeholder KINGDOM_BANK_BALANCE = fromString("kingdom_bank_balance");
    public static final Placeholder POSITION_X = fromString("position_x");
    public static final Placeholder POSITION_Y = fromString("position_y");
    public static final Placeholder POSITION_Z = fromString("position_z");
    // kingdom + user plceholders
    public static final Placeholder KINGDOM_MEMBER_ROLE_DISPLAY_NAME = fromString("kingdom_member_role_display_name");
    public static final Placeholder KINGDOM_MEMBER_ROLE_ID = fromString("kingdom_member_role_id");
    public static final Placeholder KINGDOM_MEMBER_ROLE_WEIGHT = fromString("kingdom_member_role_weight");
    public static final Placeholder KINGDOM_MEMBER_UUID = fromString("kingdom_member_uuid");
    public static final Placeholder KINGDOM_MEMBER_NAME = fromString("kingdom_member_name");
    public static final Placeholder TELEPORT_INFO_SECONDS_LEFT = fromString("teleport_info_seconds_left");
    public static final Placeholder GUI_USER_ROLE_ID = fromString("gui_user_role_id");
    public static final Placeholder GUI_USER_ROLE_WEIGHT = fromString("gui_user_role_weight");
    public static final Placeholder GUI_USER_UUID = fromString("gui_user_uuid");
    public static final Placeholder GUI_KINGDOM_ID = fromString("gui_kingdom_id");
    public static final Placeholder GUI_KINGDOM_NAME = fromString("gui_kingdom_name");
    public static final Placeholder COMMAND_HELP_NAME = fromString("command_help_name");
    public static final Placeholder COMMAND_HELP_USAGE = fromString("command_help_usage");
    public static final Placeholder COMMAND_HELP_PERMISSION = fromString("command_help_permission");
    public static final Placeholder COMMAND_INFO_PREVIOUS_PAGE_NUMBER = fromString("command_info_previous_page_number");
    public static final Placeholder COMMAND_INFO_NEXT_PAGE_NUMBER = fromString("command_info_next_page_number");

}
