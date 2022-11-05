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

package io.github.fisher2911.kingdoms;

import io.github.fisher2911.kingdoms.command.kingdom.KingdomCommand;
import io.github.fisher2911.kingdoms.config.KingdomsSettings;
import io.github.fisher2911.kingdoms.confirm.ConfirmationManager;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.EconomyManager;
import io.github.fisher2911.kingdoms.economy.PriceManager;
import io.github.fisher2911.kingdoms.gui.GuiListener;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.hook.Hooks;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.claim.MapVisualizer;
import io.github.fisher2911.kingdoms.kingdom.invite.InviteManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationManager;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeManager;
import io.github.fisher2911.kingdoms.listener.ChatListener;
import io.github.fisher2911.kingdoms.listener.ClaimEnterListener;
import io.github.fisher2911.kingdoms.listener.ClaimLoadListener;
import io.github.fisher2911.kingdoms.listener.GlobalListener;
import io.github.fisher2911.kingdoms.listener.PlayerJoinListener;
import io.github.fisher2911.kingdoms.listener.ProtectionListener;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.teleport.TeleportManager;
import io.github.fisher2911.kingdoms.user.UserManager;
import net.milkbowl.vault.economy.Economy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

public final class Kingdoms extends JavaPlugin {

    private final Logger logger = LogManager.getLogger(Kingdoms.class);

    private Hooks hooks;
    private TeleportManager teleportManager;
    private GlobalListener globalListener;
    private UpgradeManager upgradeManager;
    private KingdomManager kingdomManager;
    private InviteManager inviteManager;
    private UserManager userManager;
    private DataManager dataManager;
    private PriceManager priceManager;
    private RoleManager roleManager;
    private KingdomsSettings kingdomSettings;
    private WorldManager worldManager;
    private ClaimManager claimManager;
    private RelationManager relationManager;
    private ConfirmationManager confirmationManager;
    private EconomyManager economyManager;
    private GuiManager guiManager;
    private MapVisualizer mapVisualizer;
    private Economy economy;
    private BukkitTask saveTask;

    @Override
    public void onLoad() {
        this.hooks = new Hooks();
        this.hooks.load();
    }

    @Override
    public void onEnable() {
        if (!this.setupEconomy()) {
            logger.error("Could not find a valid economy plugin! Disabling plugin!");
            return;
        }
        final int bStatsPluginId = 16799;
        Metrics metrics = new Metrics(this, bStatsPluginId);

        // order matters
        this.teleportManager = new TeleportManager(this);
        this.dataManager = new DataManager(this);
        this.globalListener = new GlobalListener();
        this.upgradeManager = new UpgradeManager(this);
        this.userManager = new UserManager(this, new HashMap<>());
        this.priceManager = new PriceManager();
        this.roleManager = new RoleManager(this, new HashMap<>());
        this.kingdomSettings = new KingdomsSettings(this);
        this.kingdomManager = new KingdomManager(this, new HashMap<>());
        this.worldManager = new WorldManager(this, new HashMap<>());
        this.claimManager = new ClaimManager(this);
        this.inviteManager = new InviteManager(this);
        this.relationManager = new RelationManager(this);
        this.confirmationManager = new ConfirmationManager(this);
        this.economyManager = new EconomyManager(this);
        this.guiManager = new GuiManager(this);
        this.mapVisualizer = new MapVisualizer(this);

        this.registerListeners();
        this.load();
        this.registerCommands();

        this.saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::save, 0L, this.kingdomSettings.getSaveInterval());
    }

    @Override
    public void onDisable() {
        if (this.saveTask != null && !this.saveTask.isCancelled()) this.saveTask.cancel();
        this.save();
    }

    public void save() {
        this.userManager.saveDirty();
        this.kingdomManager.saveDirty();
        this.worldManager.saveDirty();
    }

    public void registerCommands() {
        final KingdomCommand kingdomCommand = new KingdomCommand(this, new HashMap<>());
        this.getCommand("kingdom").setExecutor(kingdomCommand);
    }

    public void load() {
        this.saveDefaultConfig();
        MessageHandler.load();
        KPermission.load();
        this.dataManager.load();
        this.roleManager.load();
        this.kingdomSettings.load();
        this.upgradeManager.load();
        this.relationManager.load();
        this.guiManager.load();
        Bukkit.getScheduler().runTaskLater(this, this.worldManager::populate, 20);
    }

    private void registerListeners() {
        new ProtectionListener(this).init();
        new PlayerJoinListener(this).init();
        new ClaimEnterListener(this).init();
        new GuiListener(this.globalListener).init();
        new ChatListener(this).init();
        new ClaimLoadListener(this).init();
        List.of(this.globalListener).forEach(this::registerListener);
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        this.economy = rsp.getProvider();
        return this.economy != null;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public GlobalListener getGlobalListener() {
        return globalListener;
    }

    public void reload() {
        MessageHandler.reload();
        this.roleManager.reload();
        this.upgradeManager.reload();
        this.guiManager.reload();
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
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

    public KingdomsSettings getKingdomSettings() {
        return kingdomSettings;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public RelationManager getRelationManager() {
        return relationManager;
    }

    public ConfirmationManager getConfirmationManager() {
        return confirmationManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public MapVisualizer getMapVisualizer() {
        return mapVisualizer;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Hooks getHooks() {
        return hooks;
    }
}
