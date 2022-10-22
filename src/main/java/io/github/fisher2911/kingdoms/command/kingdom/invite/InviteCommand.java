package io.github.fisher2911.kingdoms.command.kingdom.invite;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.invite.InviteManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InviteCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final InviteManager inviteManager;
    private final UserManager userManager;

    public InviteCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "invite", null, CommandSenderType.PLAYER, 1, 1, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.inviteManager = this.plugin.getInviteManager();
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previousArgs) {
        final String invitedName = args[0];
        final User invited = this.userManager.getUserByName(invitedName);
        this.kingdomManager.getKingdom(user.getKingdomId()).
                ifPresentOrElse(kingdom -> this.inviteManager.invite(kingdom, user, invited), () -> MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM));
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previousArgs) {
        MessageHandler.sendMessage(user, "/k invite [player]");
    }

    @Override
    public @Nullable List<String> getTabs(User user, String[] args, String[] previousArgs) {
        return Collections.emptyList();
    }
}
