package io.github.fisher2911.kingdoms.command.kingdom.bank;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.user.User;

import java.util.HashMap;
import java.util.Map;

public class BankCommand extends KCommand {

    public BankCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "bank", null, CommandSenderType.PLAYER, 1, 3, subCommands);
        this.addSubCommand(new BalanceSubCommand(plugin, this, new HashMap<>()));
        this.addSubCommand(new DepositSubCommand(plugin, this, new HashMap<>()));
        this.addSubCommand(new WithdrawSubCommand(plugin, this, new HashMap<>()), true);
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        this.sendHelp(user);
    }

}
