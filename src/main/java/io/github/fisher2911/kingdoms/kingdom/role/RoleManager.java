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

    public RoleManager(Kingdoms plugin, Map<String, Role> roles) {
        this.plugin = plugin;
        this.roles = roles;
        this.rolesByWeight = new SortedList<>(new ArrayList<>(), Comparator.comparingInt(Role::weight));
        this.addRole(new Role("leader", "<red>Leader</>", 0));
        this.addRole(new Role("non-member", "Non-Member", 0));
    }

    public Role getLeader() {
        if (this.rolesByWeight.isEmpty()) throw new IllegalStateException("No roles found");
        return this.rolesByWeight.get(0);
    }

    public Role getNonMember() {
        if (this.rolesByWeight.isEmpty()) throw new IllegalStateException("No roles found");
        return this.rolesByWeight.get(this.rolesByWeight.size() - 1);
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
