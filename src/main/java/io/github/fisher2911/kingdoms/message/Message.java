package io.github.fisher2911.kingdoms.message;

import io.github.fisher2911.kingdoms.util.EnumUtil;
import org.jetbrains.annotations.Nullable;

public enum Message {

    ALREADY_IN_KINGDOM,
    NO_PERMISSION_TO_CREATE_KINGDOM,
    CANNOT_AFFORD_TO_CREATE_KINGDOM,
    CANNOT_AFFORD_TO_UPGRADE,
    KINGDOM_ALREADY_EXISTS,
    CREATED_KINGDOM,
    INVALID_COMMAND_EXECUTOR,
    NO_COMMAND_PERMISSION,
    NOT_IN_KINGDOM,
    KINGDOM_NOT_FOUND,
    NO_KINGDOM_PERMISSION,
    ALREADY_CLAIMED,
    SUCCESSFUL_CHUNK_CLAIM,
    SUCCESSFUL_CHUNK_UNCLAIM,
    NOT_CLAIMED_BY_KINGDOM,
    NO_ROLE_FOUND,
    ALREADY_INVITED,
    SENT_KINGDOM_INVITE_EXPIRED,
    RECEIVED_KINGDOM_INVITE_EXPIRED,
    NEW_MEMBER_JOINED_KINGDOM,
    JOINED_KINGDOM,
    OTHER_KINGDOM_FULL,
    ALREADY_MAX_UPGRADE_LEVEL,
    UPGRADE_DOES_NOT_EXIST,
    LEVEL_UP_UPGRADE_SUCCESSFUL,
    NO_AVAILABLE_CHUNKS,
    INVITED_MEMBER,
    RECEIVED_INVITE,
    NOT_INVITED_TO_KINGDOM,
    SUCCESSFUL_RELOAD,
    KINGDOM_INFO,
    KICKED_OTHER,
    KICKED_FROM_KINGDOM,
    NOT_IN_SAME_KINGDOM,
    SET_OTHER_ROLE,
    OWN_ROLE_SET,
    SENT_RELATION_REQUEST_EXPIRED,
    RECEIVED_RELATION_REQUEST_EXPIRED,
    ACCEPTED_RELATION_REQUEST,
    RELATION_REQUEST_ACCEPTED,
    SENT_RELATION_REQUEST,
    RECEIVED_RELATION_REQUEST,
    REMOVED_RELATION_TO,
    RELATION_REMOVED_FROM,
    CANNOT_RELATE_TO_SELF_KINGDOM,
    ENTERED_KINGDOM_LAND,
    LEFT_KINGDOM_LAND,
    ENTERED_WILDERNESS_LAND,



    ;

    public String getConfigPath() {
        return this.toString().toLowerCase().replace("_", "-");
    }

    @Nullable
    public static Message fromConfigPath(String path) {
        return EnumUtil.valueOf(Message.class, path.toUpperCase().replace("-", "_"));
    }

}
