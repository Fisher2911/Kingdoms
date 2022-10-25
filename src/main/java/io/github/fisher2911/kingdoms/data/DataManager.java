package io.github.fisher2911.kingdoms.data;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.economy.Bank;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomImpl;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class DataManager {

    private final Kingdoms plugin;
    private final RoleManager roleManager;

    public DataManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.roleManager = this.plugin.getRoleManager();
    }

    public Kingdom newKingdom(User creator, String name) {
        final Role leader = this.roleManager.getLeaderRole();
        final Kingdom kingdom = new KingdomImpl(
                this.plugin,
                this.plugin.getKingdomManager().countKingdoms(),
                name,
                "<green>A default kingdom description",
                new HashMap<>(),
                new HashMap<>(),
                this.roleManager.getDefaultRolePermissions(),
                this.roleManager.getDefaultRolePermissions(),
                new HashSet<>(),
                this.plugin.getUpgradeManager().getUpgradeHolder(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                Bank.createKingdomBank(0)
        );
        for (var entry : this.plugin.getRelationManager().createRelations(kingdom).entrySet()) {
            kingdom.setRelation(entry.getKey(), entry.getValue());
        }
        kingdom.addMember(creator);
        kingdom.setRole(creator, leader);
        return kingdom;
    }

    @Nullable
    public Kingdom getKingdomByName(String name) {
        // todo
        return null;
    }

}
