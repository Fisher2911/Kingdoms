package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitUser implements User {

    private final UUID uuid;
    private final String name;
    @Nullable
    private final Player player;
    private int kingdomId;

    public BukkitUser(UUID uuid, String name, @Nullable Player player) {
        this.uuid = uuid;
        this.name = name;
        this.player = player;
        this.kingdomId = Kingdom.WILDERNESS_ID;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    @Override
    public boolean isOnline() {
        return this.player != null && this.player.isOnline();
    }

    @Override
    public void takeMoney(double amount) {

    }

    @Override
    public double getMoney() {
        // todo
        return 300;
    }

    @Override
    public int getKingdomId() {
        return this.kingdomId;
    }

    @Override
    public void setKingdomId(int id) {
        this.kingdomId = id;
    }

    @Override
    public boolean hasPermission(CommandPermission permission) {
        return player != null && player.isOnline() && player.hasPermission(permission.getValue());
    }

    @Override
    public Map<Integer, ItemStack> getInventory() {
        if (this.player == null || !this.player.isOnline()) return Collections.emptyMap();
        final Map<Integer, ItemStack> inventoryMap = new HashMap<>();
        final Inventory inventory = this.player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventoryMap.put(0, inventory.getItem(slot));
        }
        return inventoryMap;
    }

    @Override
    public boolean hasKingdom() {
        return this.kingdomId >= 0;
    }

    @Override
    public void sendMessage(Component component) {
        if (!this.isOnline()) return;
        this.player.sendMessage(component);
    }
}
