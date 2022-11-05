package io.github.fisher2911.kingdoms.command.kingdom.claim;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.CommandSenderType;
import io.github.fisher2911.kingdoms.command.KCommand;
import org.jetbrains.annotations.Nullable;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class UnclaimCommand extends KCommand {

    private final KingdomManager kingdomManager;
    private final ClaimManager claimManager;

    public UnclaimCommand(Kingdoms plugin, @Nullable KCommand parent, Map<String, KCommand> subCommands) {
        super(plugin, parent, "unclaim", null, CommandSenderType.PLAYER, 0, 2, subCommands);
        this.kingdomManager = this.plugin.getKingdomManager();
        this.claimManager = this.plugin.getClaimManager();
        this.addSubCommand(new AutoSubCommand(this.plugin, this, new HashMap<>()), true);
    }

    @Override
    public void execute(User user, String[] args, String[] previous) {
        if (!user.hasKingdom()) {
            MessageHandler.sendNotInKingdom(user);
            return;
        }
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    final Location location = user.getPlayer().getLocation();
                    final Chunk chunk = location.getChunk();
                    this.claimManager.tryUnClaim(user, kingdom, chunk);
                }, () -> MessageHandler.sendMessage(user, Message.KINGDOM_NOT_FOUND)))
                .execute();
    }

}
