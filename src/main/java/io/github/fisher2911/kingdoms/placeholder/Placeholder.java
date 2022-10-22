package io.github.fisher2911.kingdoms.placeholder;

public enum Placeholder {

    KINGDOM_ID,
    KINGDOM_NAME,

    PERMISSION_NAME,
    PERMISSION_VALUE,
    PERMISSION_VALUE_GUI_DISPLAY;

    public String asString() {
        return "%" + this.toString().toLowerCase() + "%";
    }


}
