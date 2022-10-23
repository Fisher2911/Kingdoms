package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener extends KListener {

    private final Kingdoms plugin;
    private final UserManager userManager;

    public PlayerJoinListener(Kingdoms plugin) {
        super(plugin.getGlobalListener());
        this.plugin = plugin;
        this.userManager = this.plugin.getUserManager();
    }

    @Override
    public void init() {
        this.globalListener.register(PlayerJoinEvent.class, this::onJoin);
        this.globalListener.register(PlayerQuitEvent.class, this::onQuit);
    }

    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.userManager.addUser(this.userManager.wrap(player));
    }

    public void onQuit(PlayerQuitEvent event) {
        this.userManager.removeUser(event.getPlayer().getUniqueId());
    }

}
