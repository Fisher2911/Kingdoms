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

    RELATION_DISPLAY_NAME

    ;

    public String toString() {
        return "%" + super.toString().toLowerCase() + "%";
    }


}
