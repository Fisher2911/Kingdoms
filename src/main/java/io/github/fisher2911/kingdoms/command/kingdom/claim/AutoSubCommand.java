package io.github.fisher2911.kingdoms.command.kingdom.claim;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimMode;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Chunk;
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
            MessageHandler.sendNotInKingdom(user);
            return;
        }
        final ClaimMode claimMode = ClaimMode.valueOf(previous[0].toUpperCase());
        final UUID uuid = user.getId();
        final ClaimMode previousClaimMode = this.claimManager.getClaimMode(uuid);
        if (previousClaimMode == claimMode) {
            final Message message = claimMode == ClaimMode.CLAIM ? Message.DISABLED_AUTO_CLAIM : Message.DISABLED_AUTO_UNCLAIM;
            this.claimManager.removePlayerClaimMode(uuid);
            MessageHandler.sendMessage(user, message);
            return;
        }
        this.claimManager.setClaimMode(uuid, claimMode);
        final Location location = user.getPlayer().getLocation();
        final Message message = claimMode == ClaimMode.CLAIM ? Message.ENABLED_AUTO_CLAIM : Message.ENABLED_AUTO_UNCLAIM;
        final Chunk chunk = location.getChunk();
        TaskChain.create(this.plugin)
                .runAsync(() -> {
                    if (claimMode == ClaimMode.CLAIM) {
                        this.claimManager.tryClaim(user, chunk, true);
                    } else if (claimMode == ClaimMode.UNCLAIM) {
                        this.claimManager.tryUnClaim(user, chunk, true);
                    }
                    MessageHandler.sendMessage(user, message);
                })
                .execute();
    }

    @Override
    public void sendHelp(User user, String[] args, String[] previous) {
        MessageHandler.sendMessage(user, "/k claim [auto]");
    }

}
