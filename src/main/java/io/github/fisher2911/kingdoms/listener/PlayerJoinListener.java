package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final Kingdoms plugin;
    private final UserManager userManager;

    public PlayerJoinListener(Kingdoms plugin) {
        this.plugin = plugin;
        this.userManager = this.plugin.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.userManager.addUser(this.userManager.wrap(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.userManager.removeUser(event.getPlayer().getUniqueId());
    }

}
