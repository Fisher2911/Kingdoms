package io.github.fisher2911.kingdoms.command;

public enum CommandPermission {

    CREATE_KINGDOM("kingdoms.create"),
    ADMIN_COMMAND("kingdoms.cmd.admin"),
    RELOAD_COMMAND("kingdoms.cmd.reload"),
    VIEW_OTHER_KINGDOM_INFO("kingdoms.cmd.info.other"),
    VIEW_SELF_KINGDOM_INFO("kingdoms.cmd.info.self"),


            ;

    private final String value;

    CommandPermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
