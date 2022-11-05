package io.github.fisher2911.kingdoms.command.kingdom.invite;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.invite.InviteManager;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class JoinCommand extends KCommand {

    private final InviteManager inviteManager;

    public JoinCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "join", "<kingdom>", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.inviteManager = this.plugin.getInviteManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String kingdomName = args[0];
        this.inviteManager.tryJoin(user, kingdomName);
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        return super.getTabs(user, args, previousArgs, true);
    }

}
