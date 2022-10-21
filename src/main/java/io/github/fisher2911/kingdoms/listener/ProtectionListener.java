package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectionListener implements Listener {

    private final Kingdoms plugin;
    private final KingdomManager kingdomManager;
    private final UserManager userManager;
    private final WorldManager worldManager;

    public ProtectionListener(Kingdoms plugin) {
        this.plugin = plugin;
        this.kingdomManager = this.plugin.getKingdomManager();
        this.userManager = this.plugin.getUserManager();
        this.worldManager = this.plugin.getWorldManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getState() instanceof Container) {
            if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.BREAK_CONTAINER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.MINE_BLOCK)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (block.getState() instanceof Container) {
            if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.PLACE_CONTAINER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.PLACE_BLOCK)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            this.handleRightClickBlock(event);
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            this.handlePhysicalAction(event);
            return;
        }
    }

    private void handleRightClickBlock(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;
        final Material material = block.getType();
        if (block.getState() instanceof Container) {
            if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.OPEN_CONTAINER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (material == Material.LEVER) {
            if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.USE_LEVER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (Tag.BUTTONS.isTagged(material)) {
            if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.USE_BUTTON)) {
                event.setCancelled(true);
            }
            return;
        }
    }

    private void handlePhysicalAction(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;
        final Material material = block.getType();
        if (Tag.PRESSURE_PLATES.isTagged(material)) {
            if (!this.isAllowed(event.getPlayer(), block.getLocation(), KPermission.USE_PRESSURE_PLATE)) {
                event.setCancelled(true);
            }
            return;
        }
    }

    private boolean isAllowed(Player player, Location location, KPermission permission) {
        final ClaimedChunk chunk = this.worldManager.getAt(location);
        if (chunk.isWilderness()) return true;

        return this.kingdomManager.getKingdom(chunk.getOwnedBy()).map(k ->
                        k.getPermissions().hasPermission(k.getRole(player.getUniqueId()), permission)).
                orElse(false);
    }
}
