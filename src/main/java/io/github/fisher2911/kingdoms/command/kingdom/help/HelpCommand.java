package io.github.fisher2911.kingdoms.command.kingdom.help;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.NumberUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HelpCommand extends KCommand {

    public HelpCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "help", null, CommandSenderType.PLAYER, 0, 1, subCommands);
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        if (args.length == 0) {
            this.parent.sendHelp(user);
            return;
        }
        final Integer page = NumberUtil.integerValueOf(args[0]);
        if (page == null) {
            this.parent.sendHelp(user);
            return;
        }
        this.parent.sendHelp(user, page);
    }

}
