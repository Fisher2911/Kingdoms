package io.github.fisher2911.kingdoms;

import io.github.fisher2911.kingdoms.command.kingdom.KingdomCommand;
import io.github.fisher2911.kingdoms.config.KingdomsSettings;
import io.github.fisher2911.kingdoms.confirm.ConfirmationManager;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.economy.EconomyManager;
import io.github.fisher2911.kingdoms.economy.PriceManager;
import io.github.fisher2911.kingdoms.gui.GuiListener;
import io.github.fisher2911.kingdoms.gui.GuiManager;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.claim.ClaimManager;
import io.github.fisher2911.kingdoms.kingdom.invite.InviteManager;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationManager;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeManager;
import io.github.fisher2911.kingdoms.listener.ChatListener;
import io.github.fisher2911.kingdoms.listener.ClaimEnterListener;
import io.github.fisher2911.kingdoms.listener.GlobalListener;
import io.github.fisher2911.kingdoms.listener.PlayerJoinListener;
import io.github.fisher2911.kingdoms.listener.ProtectionListener;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.teleport.TeleportManager;
import io.github.fisher2911.kingdoms.user.UserManager;
import net.milkbowl.vault.economy.Economy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class Kingdoms extends JavaPlugin {

    private final Logger logger = LogManager.getLogger(Kingdoms.class);

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
    private Economy economy;

    @Override
    public void onEnable() {
        if (!this.setupEconomy()) {
            logger.error("Could not find a valid economy plugin! Disabling plugin!");
            return;
        }

        // order matters
        this.teleportManager = new TeleportManager(this);
        this.globalListener = new GlobalListener();
        this.upgradeManager = new UpgradeManager(this);
        this.userManager = new UserManager(this, new HashMap<>());
        this.priceManager = new PriceManager();
        this.roleManager = new RoleManager(this, new HashMap<>());
        this.kingdomSettings = new KingdomsSettings(this);
        this.dataManager = new DataManager(this);
        this.kingdomManager = new KingdomManager(this, new HashMap<>());
        this.worldManager = new WorldManager(this, new HashMap<>());
        this.claimManager = new ClaimManager(this);
        this.inviteManager = new InviteManager(this);
        this.relationManager = new RelationManager(this);
        this.confirmationManager = new ConfirmationManager(this);
        this.economyManager = new EconomyManager(this);
        this.guiManager = new GuiManager(this);

        this.registerListeners();
        this.registerCommands();

        this.load();
    }

    public void registerCommands() {
        this.getCommand("kingdom").setExecutor(new KingdomCommand(this, new HashMap<>()));
    }

    public void load() {
        MessageHandler.load();
        this.roleManager.load();
        this.kingdomSettings.load();
        this.upgradeManager.load();
        this.relationManager.load();
        this.guiManager.load();
        Bukkit.getScheduler().runTaskLater(this, this.worldManager::populate, 20);
    }

    @Override
    public void onDisable() {
    }

    private void registerListeners() {
        new ProtectionListener(this).init();
        new PlayerJoinListener(this).init();
        new ClaimEnterListener(this).init();
        new GuiListener(this.globalListener).init();
        new ChatListener(this).init();
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

    public Economy getEconomy() {
        return economy;
    }
}
