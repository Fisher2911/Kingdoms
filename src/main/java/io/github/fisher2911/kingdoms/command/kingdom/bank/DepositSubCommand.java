package io.github.fisher2911.kingdoms.command.kingdom.bank;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.economy.EconomyManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.NumberUtil;
import org.bukkit.Bukkit;

import java.util.Map;

public class DepositSubCommand extends KCommand {

    private final EconomyManager economyManager;

    public DepositSubCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "deposit", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.economyManager = this.plugin.getEconomyManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final Double amount = NumberUtil.doubleValueOf(args[0]);
        if (amount == null) {
            this.sendHelp(user, args, previousArgs);
            return;
        }
        this.economyManager.tryDeposit(user, amount);
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k bank deposit <amount>");
    }

}
