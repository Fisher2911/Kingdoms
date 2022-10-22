package io.github.fisher2911.kingdoms.placeholder;

public enum Placeholder {

    KINGDOM_ID,
    KINGDOM_NAME,
    KINGDOM_MEMBERS,
    KINGDOM_DESCRIPTION,

    PERMISSION_NAME,
    PERMISSION_DISPLAY_NAME,
    PERMISSION_VALUE,
    PERMISSION_DISPLAY_VALUE,

    UPGRADE_ID,
    UPGRADE_DISPLAY_NAME,
    UPGRADE_VALUE,
    UPGRADE_DISPLAY_VALUE,
    UPGRADE_DISPLAY_PRICE,
    UPGRADE_LEVEL

    ;

    public String toString() {
        return "%" + super.toString().toLowerCase() + "%";
    }


}
