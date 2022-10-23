package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimMode;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ClaimEnterListener extends KListener {

    private final Kingdoms plugin;
    private final WorldManager worldManager;
    private final KingdomManager kingdomManager;
    private final ClaimManager claimManager;
    private final UserManager userManager;

    public ClaimEnterListener(Kingdoms plugin) {
        super(plugin.getGlobalListener());
        this.plugin = plugin;
        this.worldManager = this.plugin.getWorldManager();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.claimManager = this.plugin.getClaimManager();
        this.userManager = this.plugin.getUserManager();
    }

    public void init() {
        this.globalListener.register(PlayerMoveEvent.class, this::onPlayerMove);
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final ClaimedChunk fromChunk = this.worldManager.getAt(from);
        final ClaimedChunk toChunk = this.worldManager.getAt(to);

        if (toChunk.equals(fromChunk)) return;

        if (fromChunk.getOwnedBy() == toChunk.getOwnedBy()) {
            this.enterSameChunkToClaim(this.userManager.wrap(event.getPlayer()), toChunk);
            return;
        }
        if (fromChunk.isWilderness() && !toChunk.isWilderness()) {
            this.handleEnterKingdomLand(event, toChunk);
            return;
        }
        if (!fromChunk.isWilderness() && toChunk.isWilderness()) {
            this.handleEnterWilderness(event, fromChunk, toChunk);
            return;
        }
    }

    private void handleEnterKingdomLand(PlayerMoveEvent event, ClaimedChunk chunk) {
        final Player player = event.getPlayer();
        final User user = this.userManager.wrap(player);
        this.kingdomManager.getKingdom(chunk.getOwnedBy()).ifPresent(kingdom -> {
                    MessageHandler.sendMessage(user, Message.ENTERED_KINGDOM_LAND, kingdom);
                    final ClaimMode claimMode = this.claimManager.getClaimMode(player.getUniqueId());
                    if (claimMode == ClaimMode.UNCLAIM && kingdom.hasPermission(user, KPermission.UNCLAIM_LAND, chunk)) {
                        this.claimManager.tryUnClaim(user, kingdom, chunk);
                    }
                }
        );
    }

    private void handleEnterWilderness(PlayerMoveEvent event, ClaimedChunk previous, ClaimedChunk chunk) {
        final Player player = event.getPlayer();
        final User user = this.userManager.wrap(player);
        final ClaimMode claimMode = this.claimManager.getClaimMode(player.getUniqueId());
        if (claimMode == ClaimMode.NONE) {
            this.kingdomManager.getKingdom(previous.getOwnedBy()).
                    ifPresent(kingdom -> MessageHandler.sendMessage(user, Message.LEFT_KINGDOM_LAND, kingdom));
            MessageHandler.sendMessage(user, Message.ENTERED_WILDERNESS_LAND);
            return;
        }
        this.enterSameChunkToClaim(user, chunk);
    }

    private void enterSameChunkToClaim(User user, ClaimedChunk chunk) {
        final ClaimMode claimMode = this.claimManager.getClaimMode(user.getId());
        if (claimMode == ClaimMode.NONE) return;
        if (claimMode == ClaimMode.CLAIM && chunk.getOwnedBy() == user.getKingdomId()) return;
        if (claimMode == ClaimMode.UNCLAIM && chunk.getOwnedBy() != user.getKingdomId()) return;
        this.kingdomManager.getKingdom(user.getKingdomId()).ifPresent(kingdom -> {
            if (claimMode == ClaimMode.CLAIM) {
                if (!kingdom.hasPermission(user, KPermission.CLAIM_LAND, chunk)) return;
                this.claimManager.tryClaim(user, kingdom, chunk);
                return;
            }
            if (claimMode == ClaimMode.UNCLAIM) {
                if (!kingdom.hasPermission(user, KPermission.UNCLAIM_LAND, chunk)) return;
                this.claimManager.tryUnClaim(user, kingdom, chunk);
            }
        });
    }
}
