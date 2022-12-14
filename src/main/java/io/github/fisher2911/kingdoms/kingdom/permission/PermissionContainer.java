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

package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.fisherlib.configurate.ConfigurationNode;
import io.github.fisher2911.fisherlib.data.Saveable;
import io.github.fisher2911.fisherlib.util.MapOfMaps;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.role.RoleManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PermissionContainer implements Saveable {

    // String is role id
    private final MapOfMaps<String, KPermission, Boolean> permissions;
    private boolean dirty;

    public PermissionContainer(MapOfMaps<String, KPermission, Boolean> permissions) {
        this.permissions = permissions;
        if (!this.permissions.isEmpty()) this.dirty = true;
    }

    /**
     * returns an immutable map
     * @return
     */
    public static PermissionContainer empty() {
        return new PermissionContainer(new MapOfMaps<>(Collections.emptyMap(), Collections::emptyMap));
    }

    public boolean hasPermission(Role role, KPermission permission, @Nullable RoleManager roleManager) {
        final Boolean hasPermission = this.permissions.get(role.id(), permission);
        if (hasPermission != null) return hasPermission;
        if (roleManager == null) return false;
        final Role pluginRole = roleManager.getPluginDefaultRole(role.id());
        if (pluginRole != null) {
            final boolean defaultPerm = roleManager.getDefaultRolePermissions().hasPermission(role, permission, null);
            this.setPermission(role, permission, defaultPerm);
            return defaultPerm;
        }
        return false;
    }

    public boolean containsPermission(Role role, KPermission permission) {
        return this.permissions.getOrDefault(role.id(), Collections.emptyMap()).containsKey(permission);
    }

    public boolean ifPermissible(Role role, KPermission permission, Runnable runnable) {
        if (this.permissions.getOrDefault(role.id(), permission, false)) {
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
        this.setPermission(role.id(), permission, value);
    }

    public void setPermission(String roleId, KPermission permission, boolean value) {
        this.permissions.put(roleId, permission, value);
        this.setDirty(true);
    }


    public MapOfMaps<String, KPermission, Boolean> getPermissions() {
        return new MapOfMaps<>(this.permissions);
    }

    @Override
    public String toString() {
        return "PermissionContainer{" +
                "permissions=" + permissions +
                '}';
    }

    public PermissionContainer copy() {
        return new PermissionContainer(new MapOfMaps<>(this.permissions));
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public static PermissionContainer deserialize(ConfigurationNode node, String permissionPath) {
        final MapOfMaps<String, KPermission, Boolean> mapOfMaps = new MapOfMaps<>(new HashMap<>(), HashMap::new);
        for (var entry : node.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof String roleId)) continue;
            final ConfigurationNode roleNode = node.node(roleId).node(permissionPath);
            mapOfMaps.put(roleId, deserializePermissions(roleNode));
        }
        return new PermissionContainer(mapOfMaps);
    }

    public static Map<KPermission, Boolean> deserializePermissions(ConfigurationNode node) {
        final Map<KPermission, Boolean> permissions = new HashMap<>();
        for (var entry : node.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final String key)) continue;
            permissions.put(KPermission.getByName(key), entry.getValue().getBoolean(false));
        }
        return permissions;
    }
}
