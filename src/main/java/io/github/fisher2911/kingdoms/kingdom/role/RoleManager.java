package io.github.fisher2911.kingdoms.kingdom.role;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.util.SortedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RoleManager {

    private final Kingdoms plugin;
    private final Map<String, Role> roles;
    private final List<Role> rolesByWeight;
    private Role leaderRole;
    private Role defaultRole;
    private Role enemyRole;
    private Role neutralRole;
    private Role truceRole;
    private Role allyRole;

    public RoleManager(Kingdoms plugin, Map<String, Role> roles) {
        this.plugin = plugin;
        this.roles = roles;
        this.rolesByWeight = new SortedList<>(new ArrayList<>(), Comparator.comparingInt(Role::weight));
        this.leaderRole = new Role("leader", "<red>Leader</red>", 0);
        this.defaultRole = new Role("member", "<green>Member", 3);
        this.addRole(this.defaultRole);

        this.enemyRole = new Role("enemy", "Enemy", Integer.MAX_VALUE);
        this.neutralRole = new Role("neutral", "Neutral", Integer.MAX_VALUE - 1);
        this.truceRole = new Role("truce", "Truce", Integer.MAX_VALUE - 2);
        this.allyRole = new Role("ally", "Ally", Integer.MAX_VALUE - 3);
        this.addRole(this.enemyRole);
        this.addRole(this.neutralRole);
        this.addRole(this.truceRole);
        this.addRole(this.allyRole);
    }

    public Role getLeaderRole() {
        return this.leaderRole;
    }

    public Role getDefaultRole() {
        if (this.rolesByWeight.isEmpty()) throw new IllegalStateException("No roles found");
        return this.defaultRole;
    }

    public Role getEnemyRole() {
        return enemyRole;
    }

    public Role getNeutralRole() {
        return neutralRole;
    }

    public Role getTruceRole() {
        return truceRole;
    }

    public Role getAllyRole() {
        return allyRole;
    }

    public Role getById(String id) {
        return this.roles.get(id);
    }

    public void addRole(Role role) {
        this.roles.put(role.id(), role);
        this.rolesByWeight.add(role);
    }

    public Collection<String> getAllRoleIds() {
        return this.roles.keySet();
    }
}
