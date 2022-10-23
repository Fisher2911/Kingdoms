package io.github.fisher2911.kingdoms.command.kingdom.claim;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class ClaimCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final ClaimManager claimManager;

    public ClaimCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "claim", null, CommandSenderType.PLAYER, 0, 2, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.claimManager = this.plugin.getClaimManager();
        this.addSubCommand(new AutoSubCommand(this.plugin, new HashMap<>()));
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            MessageHandler.sendNotInKingdom(user);
            return;
        }
        this.kingdomManager.getKingdom(user.getKingdomId()).
                ifPresentOrElse(kingdom -> {
                    final Location location = user.getPlayer().getLocation();
                    this.claimManager.tryClaim(user, location);
                }, () -> MessageHandler.sendMessage(user, Message.KINGDOM_NOT_FOUND));
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k claim [auto]");
    }

}
