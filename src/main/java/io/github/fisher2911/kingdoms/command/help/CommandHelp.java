package io.github.fisher2911.kingdoms.command.help;

import org.jetbrains.annotations.Nullable;

public class CommandHelp {

    private final String command;
    private final String usage;
    @Nullable
    private final String permission;

    public CommandHelp(String command, String usage, @Nullable String permission) {
        this.command = command;
        this.usage = usage;
        this.permission = permission;
    }

    public String getCommand() {
        return this.command;
    }

    public String getUsage() {
        return this.usage;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "CommandHelp{" +
                "command='" + command + '\'' +
                ", usage='" + usage + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}
