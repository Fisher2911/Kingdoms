package io.github.fisher2911.kingdoms;

import io.github.fisher2911.kingdoms.command.kingdom.KingdomCommand;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.PriceManager;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.listener.ClaimEnterListener;
import io.github.fisher2911.kingdoms.listener.PlayerJoinListener;
import io.github.fisher2911.kingdoms.listener.ProtectionListener;
import io.github.fisher2911.kingdoms.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class Kingdoms extends JavaPlugin {

    private KingdomManager kingdomManager;
    private UserManager userManager;
    private DataManager dataManager;
    private PriceManager priceManager;
    private RoleManager roleManager;
    private WorldManager worldManager;
    private ClaimManager claimManager;

    @Override
    public void onEnable() {
        this.userManager = new UserManager(new HashMap<>());
        this.priceManager = new PriceManager();
        this.roleManager = new RoleManager(this, new HashMap<>());
        this.dataManager = new DataManager(this);
        this.kingdomManager = new KingdomManager(this, new HashMap<>());
        this.worldManager = new WorldManager(this, new HashMap<>());
        this.claimManager = new ClaimManager(this);
        this.registerListeners();
        this.registerCommands();
        Bukkit.getScheduler().runTaskLater(this, this.worldManager::populate, 20);
    }

    public void registerCommands() {
        this.getCommand("kingdom").setExecutor(new KingdomCommand(this, new HashMap<>()));
    }

    @Override
    public void onDisable() {
    }

    private void registerListeners() {
        List.of(
                        new PlayerJoinListener(this),
                        new ProtectionListener(this),
                        new ClaimEnterListener(this)
                ).
                forEach(this::registerListener);
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PriceManager getPriceManager() {
        return priceManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }
}
