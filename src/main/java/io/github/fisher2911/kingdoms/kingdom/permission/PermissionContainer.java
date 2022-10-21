package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;

public class PermissionContainer {

    private final MapOfMaps<Role, KPermission, Boolean> permissions;

    public PermissionContainer(MapOfMaps<Role, KPermission, Boolean> permissions) {
        this.permissions = permissions;
    }

    /**
     * returns an immutable map
     * @return
     */
    public static PermissionContainer empty() {
        return new PermissionContainer(new MapOfMaps<>(Collections.emptyMap(), Collections::emptyMap));
    }

    public static PermissionContainer createWithLeader(Role leader) {
        final MapOfMaps<Role, KPermission, Boolean> mapOfMaps = new MapOfMaps<>(new HashMap<>(), HashMap::new);
        for (KPermission permission : KPermission.values()) {
            mapOfMaps.put(leader, permission, true);
        }
        return new PermissionContainer(mapOfMaps);
    }

    public boolean hasPermission(Role role, KPermission permission) {
        return this.permissions.getOrDefault(role, permission, false);
    }

    public boolean ifPermissible(Role role, KPermission permission, Runnable runnable) {
        if (this.permissions.getOrDefault(role, permission, false)) {
            runnable.run();
            return true;
        }
        return false;
    }

    public boolean ifPermissibleOrElse(Role role, KPermission permission, Runnable runnable, Runnable orElse) {
        if (this.ifPermissible(role, permission, runnable)) return true;
        orElse.run();
        return false;
    }

    public void setPermission(Role role, KPermission permission, boolean value) {
        this.permissions.put(role, permission, value);
    }

    public MapOfMaps<Role, KPermission, Boolean> getPermissions() {
        return this.permissions;
    }

    @Override
    public String toString() {
        return "PermissionContainer{" +
                "permissions=" + permissions +
                '}';
    }
}
