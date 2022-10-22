package io.github.fisher2911.kingdoms.command.kingdom.claim;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimMode;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public class AutoSubCommand extends KCommand {

    private final ClaimManager claimManager;

    public AutoSubCommand(Kingdoms plugin, Map<String, KCommand> subCommands) {
        super(plugin, "auto", null, CommandSenderType.PLAYER, 0, 1, subCommands);
        this.claimManager = this.plugin.getClaimManager();
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            MessageHandler.sendMessage(user, Message.NOT_IN_KINGDOM);
            return;
        }
        final ClaimMode claimMode = ClaimMode.valueOf(previous[0].toUpperCase());
        final UUID uuid = user.getId();
        final ClaimMode previousClaimMode = this.claimManager.getClaimMode(uuid);
        if (previousClaimMode == claimMode) {
            this.claimManager.removePlayerClaimMode(uuid);
            MessageHandler.sendMessage(user, "No longer " + claimMode.toString().toLowerCase() + "ing land");
            return;
        }
        this.claimManager.setClaimMode(uuid, claimMode);
        final Location location = user.getPlayer().getLocation();
        if (claimMode == ClaimMode.CLAIM) {
            this.claimManager.tryClaim(user, location);
        } else if (claimMode == ClaimMode.UNCLAIM) {
            this.claimManager.tryUnClaim(user, location);
        }
        MessageHandler.sendMessage(user, claimMode.toString().toLowerCase() + "ing land");
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k claim [auto]");
    }

}
