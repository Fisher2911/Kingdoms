package io.github.fisher2911.kingdoms.kingdom.role;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.Config;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContainer;
import io.github.fisher2911.kingdoms.util.SortedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleManager extends Config {

    private final Kingdoms plugin;
    private final Map<String, Role> roles;
    private final List<Role> rolesByWeight;
    private PermissionContainer defaultRolePermissions;
    private Role leaderRole;
    private Role defaultRole;
    private Role enemyRole;
    private Role neutralRole;
    private Role truceRole;
    private Role allyRole;

    public RoleManager(Kingdoms plugin, Map<String, Role> roles) {
        super(plugin, "kingdom-defaults", "roles.yml");
        this.plugin = plugin;
        this.roles = roles;
        this.rolesByWeight = new SortedList<>(new ArrayList<>(), Comparator.comparingInt(Role::weight));
    }

    @Nullable
    public Role getRole(String id, Kingdom kingdom) {
        return kingdom.getRole(id);
    }

    public List<Role> getRoles(Kingdom kingdom) {
        return new SortedList<>(new ArrayList<>(kingdom.getRoles().values()), Comparator.comparingInt(Role::weight));
    }

    private static final String ROLES_PATH = "roles";
    private static final String DISPLAY_NAME_PATH = "display-name";
    private static final String WEIGHT_PATH = "weight";
    
    private static final String LEADER_ROLE_ID = "leader";
    private static final String DEFAULT_ROLE_ID = "member";
    private static final String ENEMY_ROLE_ID = "enemy";
    private static final String NEUTRAL_ROLE_ID = "neutral";
    private static final String TRUCE_ROLE_ID = "truce";
    private static final String ALLY_ROLE_ID = "ally";

    public static final String PERMISSIONS = "permissions";

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(this.path)
                .build();
        try {
            final var source = loader.load();
            
            final var roles = source.node(ROLES_PATH);
            this.leaderRole = this.loadRole(source, LEADER_ROLE_ID);
            this.defaultRole = this.loadRole(source, DEFAULT_ROLE_ID);
            this.enemyRole = this.loadRole(source, ENEMY_ROLE_ID);
            this.neutralRole = this.loadRole(source, NEUTRAL_ROLE_ID);
            this.truceRole = this.loadRole(source, TRUCE_ROLE_ID);
            this.allyRole = this.loadRole(source, ALLY_ROLE_ID);



            for (var entry : roles.childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final String id)) continue;
                if (this.roles.containsKey(id)) continue;
                this.addRole(this.loadRole(source, id));
            }
            this.defaultRolePermissions = PermissionContainer.deserialize(source.node(ROLES_PATH), PERMISSIONS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Role loadRole(ConfigurationNode source, String id) {
        final var node = source.node(ROLES_PATH, id);
        final var displayName = node.node(DISPLAY_NAME_PATH).getString("");
        final var weight = node.node(WEIGHT_PATH).getInt(0);
        final Role role = new Role(id, displayName, weight);
        this.addRole(role);
        return role;
    }
    
    public Role getLeaderRole(Kingdom kingdom) {
        return kingdom.getRole(this.leaderRole.id());
    }

    public Role getDefaultRole(Kingdom kingdom) {
        return kingdom.getRole(this.defaultRole.id());
    }

    public Role getEnemyRole(Kingdom kingdom) {
        return kingdom.getRole(this.enemyRole.id());
    }

    public Role getNeutralRole(Kingdom kingdom) {
        return kingdom.getRole(this.neutralRole.id());
    }

    public Role getTruceRole(Kingdom kingdom) {
        return kingdom.getRole(this.truceRole.id());
    }

    public Role getAllyRole(Kingdom kingdom) {
        return kingdom.getRole(this.allyRole.id());
    }

    public String getLeaderRoleId() {
        return this.leaderRole.id();
    }

    public String getDefaultRoleId() {
        return this.defaultRole.id();
    }

    public String getEnemyRoleId() {
        return this.enemyRole.id();
    }

    public String getNeutralRoleId() {
        return this.neutralRole.id();
    }

    public String getTruceRoleId() {
        return this.truceRole.id();
    }

    public String getAllyRoleId() {
        return this.allyRole.id();
    }

    public Map<String, Role> createKingdomRoles() {
        return new HashMap<>(this.roles);
    }

//    public Role getById(String id) {
//        return this.roles.get(id);
//    }

    private void addRole(Role role) {
        this.roles.put(role.id(), role);
        this.rolesByWeight.add(role);
    }

    public Collection<String> getAllRoleIds() {
        return this.roles.keySet();
    }

    public PermissionContainer getDefaultRolePermissions() {
        return defaultRolePermissions.copy();
    }
}
