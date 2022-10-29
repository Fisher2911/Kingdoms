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
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
        if (event.getTo() == null || !this.hasChangedBlock(event.getFrom(), event.getTo())) return;
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final ClaimedChunk fromChunk = this.worldManager.getAt(from);
        final ClaimedChunk toChunk = this.worldManager.getAt(to);

        if (toChunk.equals(fromChunk)) return;

        final User user = this.userManager.forceGet(event.getPlayer());
        if (user == null) return;

        if (fromChunk.getOwnedBy() == toChunk.getOwnedBy()) {
            this.enterSameChunkToClaim(user, toChunk);
            return;
        }
        if (fromChunk.isWilderness() && !toChunk.isWilderness()) {
            this.handleEnterKingdomLand(event, user, toChunk);
            return;
        }
        if (!fromChunk.isWilderness() && toChunk.isWilderness()) {
            this.handleEnterWilderness(event, user, fromChunk, toChunk);
            return;
        }
    }

    private boolean hasChangedBlock(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }

    private void handleEnterKingdomLand(PlayerMoveEvent event, User user, ClaimedChunk chunk) {
        final Player player = event.getPlayer();
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(chunk.getOwnedBy(), true))
                .consumeSync(opt -> opt.ifPresent(kingdom -> {
                            MessageHandler.sendMessage(user, Message.ENTERED_KINGDOM_LAND, kingdom);
                            final ClaimMode claimMode = this.claimManager.getClaimMode(player.getUniqueId());
                            if (claimMode == ClaimMode.UNCLAIM && kingdom.hasPermission(user, KPermission.UNCLAIM_LAND, chunk)) {
                                this.claimManager.tryUnClaim(user, kingdom, chunk);
                            }
                        }
                ))
                .execute();

    }

    private void handleEnterWilderness(PlayerMoveEvent event, User user, ClaimedChunk previous, ClaimedChunk chunk) {
        final Player player = event.getPlayer();
        final ClaimMode claimMode = this.claimManager.getClaimMode(player.getUniqueId());
        if (claimMode == ClaimMode.NONE) {
            TaskChain.create(this.plugin)
                    .supplyAsync(() -> this.kingdomManager.getKingdom(previous.getOwnedBy(), true))
                    .consumeSync(opt -> {
                        opt.ifPresent(kingdom -> MessageHandler.sendMessage(user, Message.LEFT_KINGDOM_LAND, kingdom));
                        MessageHandler.sendMessage(user, Message.ENTERED_WILDERNESS_LAND);
                    })
                    .execute();
            return;
        }
        this.enterSameChunkToClaim(user, chunk);
    }

    private void enterSameChunkToClaim(User user, ClaimedChunk chunk) {
        final ClaimMode claimMode = this.claimManager.getClaimMode(user.getId());
        if (claimMode == ClaimMode.NONE) return;
        if (claimMode == ClaimMode.CLAIM && chunk.getOwnedBy() == user.getKingdomId()) return;
        if (claimMode == ClaimMode.UNCLAIM && chunk.getOwnedBy() != user.getKingdomId()) return;
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresent(kingdom -> {
                    if (claimMode == ClaimMode.CLAIM) {
                        if (!kingdom.hasPermission(user, KPermission.CLAIM_LAND, chunk)) return;
                        this.claimManager.tryClaim(user, kingdom, chunk);
                        return;
                    }
                    if (claimMode == ClaimMode.UNCLAIM) {
                        if (!kingdom.hasPermission(user, KPermission.UNCLAIM_LAND, chunk)) return;
                        this.claimManager.tryUnClaim(user, kingdom, chunk);
                    }
                }))
                .execute();
    }
}
