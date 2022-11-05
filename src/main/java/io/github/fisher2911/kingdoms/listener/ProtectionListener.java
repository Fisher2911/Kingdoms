/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.chunk.BreakBlockInClaimEvent;
import io.github.fisher2911.kingdoms.api.event.chunk.InteractInClaimEvent;
import io.github.fisher2911.kingdoms.api.event.chunk.PlaceBlockInClaimEvent;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class ProtectionListener extends KListener {

    private final Kingdoms plugin;
    private final KingdomManager kingdomManager;
    private final UserManager userManager;
    private final WorldManager worldManager;

    public ProtectionListener(Kingdoms plugin) {
        super(plugin.getGlobalListener());
        this.plugin = plugin;
        this.kingdomManager = this.plugin.getKingdomManager();
        this.userManager = this.plugin.getUserManager();
        this.worldManager = this.plugin.getWorldManager();
    }

    public void init() {
        this.globalListener.register(BlockBreakEvent.class, this::onBlockBreak);
        this.globalListener.register(BlockPlaceEvent.class, this::onBlockPlace);
        this.globalListener.register(PlayerInteractEvent.class, this::onClickBlock);
    }

    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        final ClaimedChunk chunk = this.worldManager.getAt(block.getLocation());
        if (chunk.isWilderness()) return;
        final User user = this.userManager.forceGet(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }
        final Optional<Kingdom> optionalKingdom = this.kingdomManager.getKingdom(chunk.getKingdomId(), false);
        if (optionalKingdom.isEmpty()) {
            event.setCancelled(true);
            return;
        }
        final Kingdom kingdom = optionalKingdom.get();
        final BreakBlockInClaimEvent breakBlockInClaimEvent = new BreakBlockInClaimEvent(kingdom, chunk, event, user);
        this.plugin.getServer().getPluginManager().callEvent(breakBlockInClaimEvent);
        if (breakBlockInClaimEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        if (block.getState() instanceof Container) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.BREAK_CONTAINER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (!this.isAllowed(user, chunk, kingdom, KPermission.MINE_BLOCK)) {
            event.setCancelled(true);
            return;
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final ClaimedChunk chunk = this.worldManager.getAt(block.getLocation());
        if (chunk.isWilderness()) return;
        final User user = this.userManager.forceGet(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }
        final Optional<Kingdom> optionalKingdom = this.kingdomManager.getKingdom(chunk.getKingdomId(), false);
        if (optionalKingdom.isEmpty()) {
            event.setCancelled(true);
            return;
        }
        final Kingdom kingdom = optionalKingdom.get();
        final PlaceBlockInClaimEvent placeBlockInClaimEvent = new PlaceBlockInClaimEvent(kingdom, chunk, event, user);
        this.plugin.getServer().getPluginManager().callEvent(placeBlockInClaimEvent);
        if (placeBlockInClaimEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        if (block.getState() instanceof Container) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.PLACE_CONTAINER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (!this.isAllowed(user, chunk, kingdom, KPermission.PLACE_BLOCK)) {
            event.setCancelled(true);
        }
    }

    public void onClickBlock(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;
        final ClaimedChunk chunk = this.worldManager.getAt(block.getLocation());
        if (chunk.isWilderness()) return;
        final User user = this.userManager.forceGet(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }
        final Optional<Kingdom> optionalKingdom = this.kingdomManager.getKingdom(chunk.getKingdomId(), false);
        if (optionalKingdom.isEmpty()) {
            event.setCancelled(true);
            return;
        }
        final Kingdom kingdom = optionalKingdom.get();
        final InteractInClaimEvent interactInClaimEvent = new InteractInClaimEvent(kingdom, chunk, event, user);
        this.plugin.getServer().getPluginManager().callEvent(interactInClaimEvent);
        if (interactInClaimEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            this.handleRightClickBlock(chunk, kingdom, user, event);
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            this.handlePhysicalAction(chunk, kingdom, user, event);
            return;
        }
    }

    private void handleRightClickBlock(ClaimedChunk chunk, Kingdom kingdom, User user, PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;
        final Material material = block.getType();
        if (block.getState() instanceof Container) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.OPEN_CONTAINER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (material == Material.LEVER) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.USE_LEVER)) {
                event.setCancelled(true);
            }
            return;
        }
        if (Tag.BUTTONS.isTagged(material)) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.USE_BUTTON)) {
                event.setCancelled(true);
            }
            return;
        }
        if (Tag.FENCE_GATES.isTagged(material)) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.USE_FENCE_GATE)) {
                event.setCancelled(true);
            }
            return;
        }
        if (Tag.DOORS.isTagged(material)) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.USE_DOOR)) {
                event.setCancelled(true);
            }
            return;
        }
        if (Tag.TRAPDOORS.isTagged(material)) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.USE_TRAPDOOR)) {
                event.setCancelled(true);
            }
            return;
        }
    }

    private void handlePhysicalAction(ClaimedChunk chunk, Kingdom kingdom, User user, PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;
        final Material material = block.getType();
        if (Tag.PRESSURE_PLATES.isTagged(material)) {
            if (!this.isAllowed(user, chunk, kingdom, KPermission.USE_PRESSURE_PLATE)) {
                event.setCancelled(true);
            }
            return;
        }
    }

    private boolean isAllowed(User user, ClaimedChunk chunk, Kingdom kingdom, KPermission permission) {
        if (chunk.isWilderness()) return true;
        return kingdom.hasPermission(user, permission, chunk);
    }
}
