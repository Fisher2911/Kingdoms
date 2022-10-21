package io.github.fisher2911.kingdoms.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum CommandSenderType {

    PLAYER,
    CONSOLE,
    ANY;

    public boolean canExecute(CommandSender sender) {
        if (this == ANY) return true;
        if (sender instanceof Player) return this == PLAYER;
        return this == CONSOLE;
    }

}
