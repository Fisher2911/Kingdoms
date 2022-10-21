package io.github.fisher2911.kingdoms.user;

import io.github.fisher2911.kingdoms.command.CommandPermission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface User {

    User CONSOLE = new ConsoleUser();

    UUID getId();
    String getName();
    @Nullable
    Player getPlayer();
    @Nullable
    OfflinePlayer getOfflinePlayer();
    boolean isOnline();
    void takeMoney(double amount);
    double getMoney();
    int getKingdomId();
    void setKingdomId(int id);
    boolean hasPermission(CommandPermission permission);
    Map<Integer, ItemStack> getInventory();
    boolean hasKingdom();

}
