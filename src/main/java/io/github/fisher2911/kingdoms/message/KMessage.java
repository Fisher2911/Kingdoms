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

package io.github.fisher2911.kingdoms.message;

import io.github.fisher2911.fisherlib.message.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KMessage {

    private static final Map<String, Message> messages = new HashMap<>();

    public static Collection<Message> values() {
        return messages.values();
    }

    private KMessage() {
    }

    public static final Message ALREADY_IN_KINGDOM = path("already-in-kingdom");
    public static final Message NO_PERMISSION_TO_CREATE_KINGDOM = path("no-permission-to-create-kingdom");
    public static final Message CANNOT_AFFORD_TO_CREATE_KINGDOM = path("cannot-afford-to-create-kingdom");
    public static final Message CANNOT_AFFORD_TO_UPGRADE = path("cannot-afford-to-upgrade");
    public static final Message KINGDOM_ALREADY_EXISTS = path("kingdom-already-exists");
    public static final Message CREATED_KINGDOM = path("created-kingdom");
    public static final Message INVALID_COMMAND_EXECUTOR = path("invalid-command-executor");
    public static final Message NO_COMMAND_PERMISSION = path("no-command-permission");
    public static final Message NOT_IN_KINGDOM = path("not-in-kingdom");
    public static final Message KINGDOM_NOT_FOUND = path("kingdom-not-found");
    public static final Message NO_KINGDOM_PERMISSION = path("no-kingdom-permission");
    public static final Message CANNOT_EDIT_KINGDOM_PERMISSION = path("cannot-edit-kingdom-permission");
    public static final Message ALREADY_CLAIMED = path("already-claimed");
    public static final Message SUCCESSFUL_CHUNK_CLAIM = path("successful-chunk-claim");
    public static final Message SUCCESSFUL_CHUNK_UNCLAIM = path("successful-chunk-unclaim");
    public static final Message NOT_CLAIMED_BY_KINGDOM = path("not-claimed-by-kingdom");
    public static final Message NO_ROLE_FOUND = path("no-role-found");
    public static final Message ALREADY_INVITED = path("already-invited");
    public static final Message SENT_KINGDOM_INVITE_EXPIRED = path("sent-kingdom-invite-expired");
    public static final Message RECEIVED_KINGDOM_INVITE_EXPIRED = path("received-kingdom-invite-expired");
    public static final Message NEW_MEMBER_JOINED_KINGDOM = path("new-member-joined-kingdom");
    public static final Message JOINED_KINGDOM = path("joined-kingdom");
    public static final Message OTHER_KINGDOM_FULL = path("other-kingdom-full");
    public static final Message ALREADY_MAX_UPGRADE_LEVEL = path("already-max-upgrade-level");
    public static final Message UPGRADE_DOES_NOT_EXIST = path("upgrade-does-not-exist");
    public static final Message LEVEL_UP_UPGRADE_SUCCESSFUL = path("level-up-upgrade-successful");
    public static final Message NO_AVAILABLE_CHUNKS = path("no-available-chunks");
    public static final Message INVITED_MEMBER = path("invited-member");
    public static final Message RECEIVED_INVITE = path("received-invite");
    public static final Message NOT_INVITED_TO_KINGDOM = path("not-invited-to-kingdom");
    public static final Message SUCCESSFUL_RELOAD = path("successful-reload");
    public static final Message KINGDOM_INFO = path("kingdom-info");
    public static final Message KICKED_OTHER = path("kicked-other");
    public static final Message KICKED_FROM_KINGDOM = path("kicked-from-kingdom");
    public static final Message NOT_IN_SAME_KINGDOM = path("not-in-same-kingdom");
    public static final Message SET_OTHER_ROLE = path("set-other-role");
    public static final Message OWN_ROLE_SET = path("own-role-set");
    public static final Message SENT_RELATION_REQUEST_EXPIRED = path("sent-relation-request-expired");
    public static final Message RECEIVED_RELATION_REQUEST_EXPIRED = path("received-relation-request-expired");
    public static final Message ACCEPTED_RELATION_REQUEST = path("accepted-relation-request");
    public static final Message RELATION_REQUEST_ACCEPTED = path("relation-request-accepted");
    public static final Message SENT_RELATION_REQUEST = path("sent-relation-request");
    public static final Message RECEIVED_RELATION_REQUEST = path("received-relation-request");
    public static final Message REMOVED_RELATION_TO = path("removed-relation-to");
    public static final Message RELATION_REMOVED_FROM = path("relation-removed-from");
    public static final Message CANNOT_RELATE_TO_SELF_KINGDOM = path("cannot-relate-to-self-kingdom");
    public static final Message ENTERED_KINGDOM_LAND = path("entered-kingdom-land");
    public static final Message LEFT_KINGDOM_LAND = path("left-kingdom-land");
    public static final Message ENTERED_WILDERNESS_LAND = path("entered-wilderness-land");
    public static final Message LEADER_CANNOT_LEAVE_KINGDOM = path("leader-cannot-leave-kingdom");
    public static final Message MEMBER_LEFT_KINGDOM = path("member-left-kingdom");
    public static final Message YOU_LEFT_KINGDOM = path("you-left-kingdom");
    public static final Message CONFIRM_DISBAND_KINGDOM = path("confirm-disband-kingdom");
    public static final Message DISBAND_KINGDOM_CONFIRMATION_EXPIRED = path("disband-kingdom-confirmation-expired");
    public static final Message KINGDOM_DISBANDED = path("kingdom-disbanded");
    public static final Message MUST_BE_IN_KINGDOM_TO_CHANGE_CHAT = path("must-be-in-kingdom-to-change-chat");
    public static final Message CHAT_CHANNEL_CHANGED = path("chat-channel-changed");
    public static final Message CHAT_CHANNEL_NOT_FOUND = path("chat-channel-not-found");
    public static final Message KINGDOM_BANK_WITHDRAW_SUCCESS = path("kingdom-bank-withdraw-success");
    public static final Message KINGDOM_BANK_DEPOSIT_SUCCESS = path("kingdom-bank-deposit-success");
    public static final Message NOT_ENOUGH_FUNDS = path("not-enough-funds");
    public static final Message KINGDOM_BANK_NOT_LARGE_ENOUGH = path("kingdom-bank-not-large-enough");
    public static final Message USER_NOT_ENOUGH_MONEY = path("user-not-enough-money");
    public static final Message KINGDOM_BALANCE = path("kingdom-balance");
    public static final Message MAX_RELATIONS_REACHED = path("max-relations-reached");
    public static final Message SET_KINGDOM_HOME = path("set-kingdom-home");
    public static final Message TELEPORT_SUCCESS = path("teleport-success");
    public static final Message TELEPORT_CANCELLED_MOVEMENT = path("teleport-cancelled-movement");
    public static final Message TELEPORT_CANCELLED = path("teleport-cancelled");
    public static final Message TELEPORT_COUNTDOWN = path("teleport-countdown");
    public static final Message KINGDOM_LOCATION_NOT_SET = path("kingdom-location-not-set");
    public static final Message USER_DATA_LOAD_ERROR = path("user-data-load-error");
    public static final Message INVALID_POSITION = path("invalid-position");
    public static final Message ENABLED_AUTO_CLAIM = path("enabled-auto-claim");
    public static final Message ENABLED_AUTO_UNCLAIM = path("enabled-auto-unclaim");
    public static final Message DISABLED_AUTO_CLAIM = path("disabled-auto-claim");
    public static final Message DISABLED_AUTO_UNCLAIM = path("disabled-auto-unclaim");
    public static final Message PLAYER_NOT_FOUND = path("player-not-found");
    public static final Message CANNOT_SET_PERMISSION_ROLE = path("cannot-set-permission-role");
    public static final Message ROLE_DOES_NOT_EXIST = path("role-does-not-exist");
    public static final Message CANNOT_SET_SELF_ROLE = path("cannot-set-self-role");
    public static final Message KINGDOM_DESCRIPTION = path("kingdom-description");
    public static final Message INVALID_KINGDOM_NAME_LENGTH = path("invalid-kingdom-name-length");
    public static final Message INVALID_KINGDOM_DESCRIPTION_LENGTH = path("invalid-kingdom-description-length");
    public static final Message CHANGED_KINGDOM_DESCRIPTION = path("changed-kingdom-description");
    public static final Message CHANGED_KINGDOM_NAME = path("changed-kingdom-name");
    public static final Message KINGDOM_NAME_INFO = path("kingdom-name-info");
    public static final Message COMMAND_HELP_FORMAT = path("command-help-format");
    public static final Message COMMAND_HELP_HEADER = path("command-help-header");
    public static final Message COMMAND_HELP_FOOTER = path("command-help-footer");
    public static final Message ADMIN_COMMAND_HELP_FORMAT = path("admin-command-help-format");
    public static final Message ADMIN_COMMAND_HELP_HEADER = path("admin-command-help-header");
    public static final Message ADMIN_COMMAND_HELP_FOOTER = path("admin-command-help-footer");
    public static final Message CANNOT_INVITE_SELF = path("cannot-invite-self");
    public static final Message CANNOT_CLAIM_HERE = path("cannot-claim-here");

    private static Message path(String path) {
        final Message message = Message.path(path);
        messages.put(message.getConfigPath(), message);
        return message;
    }

}
