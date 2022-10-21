package io.github.fisher2911.kingdoms.message;

import io.github.fisher2911.kingdoms.util.EnumUtil;
import org.jetbrains.annotations.Nullable;

public enum Message {

    ALREADY_IN_KINGDOM,
    NO_PERMISSION_TO_CREATE_KINGDOM,
    CANNOT_AFFORD_TO_CREATE_KINGDOM,
    KINGDOM_ALREADY_EXISTS,
    CREATED_KINGDOM,
    INVALID_COMMAND_EXECUTOR,
    NO_COMMAND_PERMISSION,
    NOT_IN_KINGDOM,
    KINGDOM_NOT_FOUND,
    NO_KINGDOM_PERMISSION,
    ALREADY_CLAIMED,
    SUCCESSFUL_CHUNK_CLAIM,
    NO_CLAIM_PERMISSION;

    public String getConfigPath() {
        return ALREADY_IN_KINGDOM.toString().toLowerCase().replace("_", "-");
    }

    @Nullable
    public static Message fromConfigPath(String path) {
        return EnumUtil.valueOf(Message.class, path.toUpperCase().replace("-", "_"));
    }

}
