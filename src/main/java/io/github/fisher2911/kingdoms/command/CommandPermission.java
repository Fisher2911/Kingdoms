package io.github.fisher2911.kingdoms.command;

public enum CommandPermission {

    CREATE_KINGDOM("kingdoms.create");

    private final String value;

    CommandPermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
