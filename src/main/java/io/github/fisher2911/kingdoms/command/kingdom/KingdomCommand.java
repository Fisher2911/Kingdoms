package io.github.fisher2911.kingdoms.command.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.command.kingdom.claim.ClaimCommand;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KingdomCommand extends KCommand implements TabExecutor, TabCompleter {

    public KingdomCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "kingdom", null, CommandSenderType.ANY, -1, -1, subCommands);
        this.addSubCommand(new CreateCommand(this.plugin, new HashMap<>()));
        this.addSubCommand(new ClaimCommand(this.plugin, new HashMap<>()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.handleArgs(sender, args, new String[0]);
        return true;
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "test: " + Arrays.toString(args) + " - " + Arrays.toString(previous));
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/test: " + Arrays.toString(args) + " - " + Arrays.toString(previous));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return this.getTabs(args, new String[0]);
    }

}
