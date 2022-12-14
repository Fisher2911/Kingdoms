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

package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.CommandPermission;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.fisherlib.world.WorldPosition;
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

    private final Kingdoms plugin;
    private final UUID uuid;
    private String name;
    @Nullable
    private Player player;
    private int kingdomId;
    private ChatChannel chatChannel;
    private boolean dirty;

    public BukkitUser(Kingdoms plugin, UUID uuid, String name, @Nullable Player player) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.name = name;
        this.player = player;
        this.kingdomId = Kingdom.WILDERNESS_ID;
        this.chatChannel = ChatChannel.GLOBAL;
    }

    public BukkitUser(Kingdoms plugin, UUID uuid, String name, @Nullable Player player, int kingdomId, ChatChannel chatChannel) {
        this(plugin, uuid, name, player);
        this.kingdomId = kingdomId;
        this.chatChannel = chatChannel;
    }

    public BukkitUser(Kingdoms plugin, Player player, int kingdomId, ChatChannel chatChannel) {
        this(plugin, player.getUniqueId(), player.getName(), player);
        this.kingdomId = kingdomId;
        this.chatChannel = chatChannel;
    }

    @Override
    public void onJoin(Player player) {
        this.player = player;
        this.name = this.player.getName();
        this.dirty = true;
    }

    @Override
    public void onQuit() {
        this.player = null;
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
        this.plugin.getEconomy().withdrawPlayer(this.getOfflinePlayer(), amount);
    }

    @Override
    public double getMoney() {
        return this.plugin.getEconomy().getBalance(this.getOfflinePlayer());
    }

    @Override
    public void addMoney(double amount) {
        this.plugin.getEconomy().depositPlayer(this.getOfflinePlayer(), amount);
    }

    @Override
    public int getKingdomId() {
        return this.kingdomId;
    }

    @Override
    public void setKingdomId(int id) {
        this.kingdomId = id;
        this.setDirty(true);
    }

    @Override
    public boolean hasPermission(CommandPermission permission) {
        return player != null && player.isOnline() && player.hasPermission(permission.getValue());
    }

    @Override
    public boolean hasPermission(String permission) {
        return player != null && player.isOnline() && player.hasPermission(permission);
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
    public ChatChannel getChatChannel() {
        return this.chatChannel;
    }

    @Override
    public void setChatChannel(ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
        this.dirty = true;
    }

    @Override
    @Nullable
    public WorldPosition getPosition() {
        if (!this.isOnline()) return null;
        return WorldPosition.fromLocation(this.player.getLocation());
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = true;
    }

    @Override
    public String toString() {
        return "BukkitUser{" +
                "plugin=" + plugin +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", player=" + player +
                ", kingdomId=" + kingdomId +
                ", chatChannel=" + chatChannel +
                ", dirty=" + dirty +
                '}';
    }
}
